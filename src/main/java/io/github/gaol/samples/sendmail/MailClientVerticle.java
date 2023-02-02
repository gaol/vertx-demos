package io.github.gaol.samples.sendmail;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;

public class MailClientVerticle extends AbstractVerticle {
  MailClient mailClient;
  private final static Logger logger = LoggerFactory.getLogger("MailClientVerticle");

  @Override
  public void start() {
    MailConfig mailConfig = new MailConfig()
      .setPort(9025)
      .setMaxPoolSize(10)
      .setHostname("127.0.0.1")
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
