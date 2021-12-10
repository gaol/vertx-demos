package io.github.gaol.samples.sendmail;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

  private final static Logger logger = LoggerFactory.getLogger("mail_verticle");

  private Router router;

  static final String MAIL_SERVICE_ADDR = "send.email";

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    router = Router.router(vertx);
    router.route("/sendmail").handler(this::sendMail);
    vertx.createHttpServer().requestHandler(router)
            .listen(Integer.getInteger("http.port", 8080), System.getProperty("http.host", "127.0.0.1"))
            .onSuccess(hs -> logger.info("Http Server started on " + hs.actualPort()))
            .flatMap(http -> startPromise.future());
  }

  private void sendMail(RoutingContext ctx) {
    try {
      SMTPAware smTPAware = new SMTPAware(vertx);
      EmailVendor vendor = EmailVendor.valueOf(ctx.request().getParam("vendor").toUpperCase());
      MailClient mailClient = MailClient.create(vertx, smTPAware.mailConfig(vendor));
      final long closeClientTimer = Long.parseLong(ctx.request().getParam("closeTimeout", "30000"));
      mailClient.sendMail(smTPAware.emailMessage(vendor)).onComplete(r -> {
        if (r.succeeded()) {
          logger.info("Email Sent: " + r.result() + ", with vendor: " + vendor.name());
          ctx.end("Mail Sent, with vendor: " + vendor.name());
        } else {
          logger.error("Failed to send email", r.cause());
          ctx.fail(500, r.cause());
        }
        logger.info("Will wait for " + closeClientTimer + " ms to close the client.");
        vertx.setTimer(closeClientTimer, l -> {
          logger.info("Now, close the mail Client for: " + vendor.name());
          mailClient.close();
        });
      });
    } catch (Exception e) {
      logger.error("Failed to send email, return 400.", e);
      ctx.fail(400);
    }
  }

  @Override
  public void stop() throws Exception {
    router.clear();
  }

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(new MainVerticle());
  }

}
