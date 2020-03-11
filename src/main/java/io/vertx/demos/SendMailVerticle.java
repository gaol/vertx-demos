package io.vertx.demos;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.SelfSignedCertificate;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.StartTLSOptions;

import java.util.concurrent.CountDownLatch;

public class SendMailVerticle extends AbstractVerticle {

  MailClient mailClient;

  @Override
  public void start() {
    System.out.println("Create MailClient from vertx Thread: " + Thread.currentThread());
    MailConfig mailConfig = new MailConfig()
            .setPort(9465)
            .setHostname("127.0.0.1")
            .setTrustAll(true)
            .setStarttls(StartTLSOptions.OPTIONAL)
            .setUsername("testa@localtest.tld")
            .setPassword("testa");
    mailClient = MailClient.createShared(vertx, mailConfig);
    startSending();
  }

  private void startSending() {
    int count = 10000; // 10k mails for each thread
    for (int i = 0; i < count; i ++) {
      MailMessage message = new MailMessage();
      message
              .setText("中文")
              .setFrom("testa@localtest.tld")
              .setTo("testb@localtest.tld")
              .setSubject("Message in Thread: " + Thread.currentThread() + ", number: " + i);

      mailClient.sendMail(message, r -> {
        if (r.succeeded()) {
          System.out.println("Message sent at: " + Thread.currentThread() + " for message: ==== " + message.getSubject());
        } else {
          r.cause().printStackTrace();
          System.exit(100);
        }
      });
    }
  }

  @Override
  public void stop() throws Exception {
    mailClient.close();
  }

}