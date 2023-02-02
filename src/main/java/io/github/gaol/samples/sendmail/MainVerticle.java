package io.github.gaol.samples.sendmail;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.CountDownLatch;


public class MainVerticle extends AbstractVerticle {
  private final static Logger logger = LoggerFactory.getLogger("MainVerticle");
  private Router router;
  private MailClientVerticle mailClientVerticle;
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    router = Router.router(vertx);
    router.route("/sendmail").handler(this::sendmail);
    mailClientVerticle = new MailClientVerticle();
    vertx.deployVerticle(SendMailVerticle.class, new DeploymentOptions().setInstances(8), did -> {
      if (did.succeeded()) {
        vertx.createHttpServer()
          .requestHandler(router)
          .listen(8888, http -> {
            if (http.succeeded()) {
              startPromise.complete();
              logger.info("HTTP server started on port 8888");
            } else {
              startPromise.fail(http.cause());
            }
          })
        ;
      } else {
        startPromise.fail(did.cause());
      }
    });
  }

  private void sendmail(RoutingContext ctx) {
    JsonObject m = new JsonObject()
      .put("total", ctx.request().getParam("total"))
      .put("subject", ctx.request().getParam("subject"))
      .put("content", ctx.request().getParam("content"));
    vertx.eventBus().send("mail.sent", m);
    ctx.end("Sent!");
  }

  private void sendmailb(RoutingContext ctx) {
    if ("1".equals(ctx.request().getParam("reset"))) {
      SendMailVerticle.clearStatistics();
      ctx.end("statistics reset");
      return;
    }
    MailMessage message = new MailMessage();
    message
      .setText(ctx.request().getParam("content"))
      .setFrom("testa@localtest.tld")
      .setTo("testb@localtest.tld")
      .setSubject(ctx.request().getParam("subject"))
    ;
    final Thread t1 = Thread.currentThread();
    mailClientVerticle.mailClient.sendMail(message).onComplete(mr -> {
      Thread t2 = Thread.currentThread();
      logger.info("t1: " + t1 + ", t2: " + t2);
      if (!t1.equals(t2)) {
        throw new IllegalStateException("context wrong !");
      }
      if (mr.succeeded()) {
        logger.info("Sent in context: " + Vertx.currentContext());
      } else {
        logger.info("Failed in context: " + Vertx.currentContext());
        mr.cause().printStackTrace();
      }
    });
    ctx.end("Send B !");
  }

  @Override
  public void stop() throws Exception {
    router.clear();
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");
    vertx.deployVerticle(new MainVerticle());
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      CountDownLatch latch = new CountDownLatch(1);
      System.out.println("Going to call vertx.close");
      vertx.close(v -> {
        latch.countDown();
        System.out.println("Vertx closed !!");
      });
      try {
        latch.await();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }));
  }
}
