package io.github.gaol.samples.sendmail;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;

public class SendMailVerticle extends AbstractVerticle {

  private MailClient mailClient;
  private final static Logger logger = LoggerFactory.getLogger("SendMailVerticle");

  static MailClient mailClient(Vertx vertx) {
    return MailClient.createShared(vertx, new MailConfig()
            .setPort(9025)
            .setMaxPoolSize(10)
            .setHostname("127.0.0.1")
            .setUsername("testa")
            .setPassword("testa"));
  }

  private MailMessage getMailMessage(JsonObject body) {
    MailMessage message = new MailMessage();
    message.setFrom(body.getString("from", "testa@localtest.tld"))
           .setTo(body.getString("to", "testb@localtest.tld"))
           .setSubject(body.getString("subject", "may the world peace"))
           .setText(body.getString("content", "world peace forever!"));
    return message;
  }

  @Override
  public void start() throws Exception {
    mailClient = mailClient(vertx);
    vertx.eventBus().<JsonObject>consumer(MainVerticle.MAIL_SERVICE_ADDR, m -> {
      MailMessage message = getMailMessage(m.body());
      logger.info("message to be sent: \n" + message.toJson().encodePrettily());
      mailClient.sendMail(message)
              .onComplete(mr -> {
                if (mr.succeeded()) {
                  if (m.replyAddress() != null) {
                    m.reply(new JsonObject().put("result", mr.result().toString()));
                  } else {
                    logger.info("Message Sent: " + mr.result());
                  }
                } else {
                  if (m.replyAddress() != null) {
                    m.fail(500, mr.cause().getMessage());
                  } else {
                    logger.error("Failed to send email", mr.cause());
                  }
                }
              });
    });
  }

  @Override
  public void stop() throws Exception {
    mailClient.close();
  }

}
