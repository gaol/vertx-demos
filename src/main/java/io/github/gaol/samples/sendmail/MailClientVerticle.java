package io.github.gaol.samples.sendmail;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.StartTLSOptions;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MailClientVerticle extends AbstractVerticle {
  MailClient mailClient;
  private final static Logger logger = Logger.getLogger("MailClientVerticle");

  @Override
  public void start() {
    MailConfig mailConfig = new MailConfig()
      .setPort(9465)
      .setMaxPoolSize(10)
      .setHostname("127.0.0.1")
      .setTrustAll(true)
      .setStarttls(StartTLSOptions.OPTIONAL)
      .setUsername("testa@localtest.tld")
      .setPassword("testa");
    mailClient = MailClient.createShared(vertx, mailConfig, "Different");
    logger.info("MailClientVerticle deployed on thread: " + Thread.currentThread());
  }

  @Override
  public void stop() {
    mailClient.close();
  }

}
