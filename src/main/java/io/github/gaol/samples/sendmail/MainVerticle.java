package io.github.gaol.samples.sendmail;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.logging.Logger;

public class MainVerticle extends AbstractVerticle {

  private final static Logger logger = Logger.getLogger("MainVerticle");

  private Router router;
  private MailClientVerticle mailClientVerticle;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    router = Router.router(vertx);
    router.route("/sendmail").handler(this::sendmail);
    router.route("/sendmailb").handler(this::sendmailb);
    mailClientVerticle = new MailClientVerticle();
    vertx.deployVerticle(mailClientVerticle);
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
    if ("1".equals(ctx.request().getParam("reset"))) {
      SendMailVerticle.clearStatistics();
      ctx.end("statistics reset");
      return;
    }
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
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");
    Vertx.vertx().deployVerticle(new MainVerticle());
  }
}
