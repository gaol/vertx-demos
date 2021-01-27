package io.github.gaol.samples.sendmail;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
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

public class SendMailVerticle extends AbstractVerticle {
  private MailClient mailClient;
  private final static Logger logger = Logger.getLogger("SendMailVerticle");

  private static final AtomicLong sent = new AtomicLong();
  private static int idx;
  private int i;

  @Override
  public void start() throws Exception {
    i = ++idx;
    MailConfig mailConfig = new MailConfig()
      .setPort(9465)
      .setMaxPoolSize(10)
      .setHostname("127.0.0.1")
      .setTrustAll(true)
      .setStarttls(StartTLSOptions.OPTIONAL)
      .setUsername("testa@localtest.tld")
      .setPassword("testa");
    mailClient = MailClient.createShared(vertx, mailConfig);

    logger.info("SendMailVerticle deployed on thread: " + Thread.currentThread());
    long start = System.currentTimeMillis();
    vertx.eventBus().<JsonObject>consumer("mail.sent", m -> {
      JsonObject body = m.body();
      MailMessage message = new MailMessage();
      message
        .setText(body.getString("content"))
        .setFrom("testa@localtest.tld")
        .setTo("testb@localtest.tld")
      .setSubject(body.getString("subject") + " in thread: " + Thread.currentThread() + ", send verticle: " + i)
      ;
      logger.info("Send Email With Subject: " + message.getSubject());
      final Thread t1 = Thread.currentThread();
      logger.info("Current Context outside of mailClient.sentMail(): " + Vertx.currentContext());
      // HERE IS DuplicatedContext with new connection pool.
      mailClient.sendMail(message, r -> {
        logger.info("Current Context inside of mailClient.sentMail(): " + Vertx.currentContext());
        Thread t2 = Thread.currentThread();
        logger.info("t1: " + t1 + ", t2: " + t2);
        if (!t1.equals(t2)) {
          logger.info("Current Context Is Wrong: " + i);
          throw new IllegalStateException("context wrong !!!!!!!!!!!!!!!!!!!!!!!!");
        }
        logger.info("event loop of context 1: " + ((ContextInternal)context).nettyEventLoop() + ", context: " + context);
        logger.info("event loop of context 2: " + ((ContextInternal)Vertx.currentContext()).nettyEventLoop() + ", context: " + Vertx.currentContext());
        if (r.succeeded()) {
          logger.info("Message sent with result: " + r.result() + ", total sent: " + sent.incrementAndGet());
        } else {
          logger.log(Level.WARNING, "Failed to send email.", r.cause());
          r.cause().printStackTrace();
        }
      });
    });
//    vertx.setTimer(2000, l -> checkClosed(1000, start));

  }

  private synchronized void checkClosed(long period, long start) {
    if (sent.get() >= 2000) {
      long time = (System.currentTimeMillis() - start) / 1000;
      logger.info("2000 mails sent, close mail client after 10 minutes. time: " + time + " S.");
//      mailClient.close();
      vertx.setTimer(600000, l -> mailClient.close());
    } else {
      vertx.setTimer(period, l -> checkClosed(period, start));
    }
  }

  @Override
  public void stop() throws Exception {
    mailClient.close();
  }

}
