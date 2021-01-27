package io.github.gaol.samples.sendmail;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.logging.Logger;

public class MainVerticle extends AbstractVerticle {

  private final static Logger logger = Logger.getLogger("MainVerticle");

  private Router router;
  private static int idx;
  private int i;
  private MailClientVerticle mailClientVerticle;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    i = ++idx;
    router = Router.router(vertx);
    router.route("/sendmail").handler(this::sendmail);
    router.route("/sendmailb").handler(this::sendmailb);
    mailClientVerticle = new MailClientVerticle();
    vertx.deployVerticle(mailClientVerticle);
    vertx.deployVerticle(SendMailVerticle.class, new DeploymentOptions().setInstances(8), did -> {
      if (did.succeeded()) {
        logger.info("Deployed : " + did.result());
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
      .put("subject", ctx.request().getParam("subject") + ", from main verticle: " + i)
      .put("content", ctx.request().getParam("content"));
    vertx.eventBus().send("mail.sent", m);
    ctx.end("Sent!");
  }

  private void sendmailb(RoutingContext ctx) {
    MailMessage message = new MailMessage();
    message
      .setText(ctx.request().getParam("content"))
      .setFrom("testa@localtest.tld")
      .setTo("testb@localtest.tld")
      .setSubject(ctx.request().getParam("subject") + " in thread: " + Thread.currentThread())
    ;
    logger.info("Will send in context: " + context);
    logger.info("Send Email With Subject: " + message.getSubject());
    final Thread t1 = Thread.currentThread();
    mailClientVerticle.mailClient.sendMail(message).onComplete(mr -> {
      ctx.end("Sent B !");
      Thread t2 = Thread.currentThread();
      logger.info("t1: " + t1 + ", t2: " + t2);
      if (!t1.equals(t2)) {
        logger.info("Current Context Is Wrong: " + i);
        throw new IllegalStateException("context wrong !!!!!!!!!!!!!!!!!!!!!!!!");
      }
      logger.info("event loop of context 1: " + ((ContextInternal)context).nettyEventLoop() + ", context: " + context);
      logger.info("event loop of context 2: " + ((ContextInternal)Vertx.currentContext()).nettyEventLoop() + ", context: " + Vertx.currentContext());
      if (mr.succeeded()) {
        logger.info("Sent in context: " + Vertx.currentContext());
      } else {
        logger.info("Failed in context: " + Vertx.currentContext());
        mr.cause().printStackTrace();
      }
    });
  }

  @Override
  public void stop() throws Exception {
    router.clear();
  }

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(new MainVerticle());
  }
}
