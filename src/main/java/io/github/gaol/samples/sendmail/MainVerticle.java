package io.github.gaol.samples.sendmail;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailMessage;
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
      MailMessage mailMessage = smTPAware.emailMessage(vendor);
      updateToMailMessage(mailMessage);
      logger.info("mail message got updated: \nfrom: \n" + mailMessage.getFrom() + ",\n html: \n" + mailMessage.getHtml());
      mailClient.sendMail(mailMessage).onComplete(r -> {
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

  private void updateToMailMessage(MailMessage mailMessage) {
    StringBuilder sb = new StringBuilder("<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<title>email title</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>This is the head 1 line</h1>\n" +
            "<a href=\"https://this_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_charactersthis_is_a_very_long_link_than_1000_ascii_characters\">Here is a very long link exceeding 1000 characters</a>\n" +
            "</body>\n" +
            "</html>");
    mailMessage.setText(null);
    mailMessage.setHtml(sb.toString());
    mailMessage.setTo("aoingl@gmail.com");
  }

  @Override
  public void stop() throws Exception {
    router.clear();
  }

  public static void main(String[] args) {
    Vertx.vertx().deployVerticle(new MainVerticle());
  }

}
