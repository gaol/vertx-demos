package io.vertx.demos;

import io.vertx.core.Vertx;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.StartTLSOptions;

public class SendMail {
    public static void main(String[] args) throws Exception {

        // testUser@testdomain
        Vertx vertx = Vertx.vertx();
        MailConfig mailConfig = new MailConfig();
        System.out.println("Create MailClient from Main Thread: " + Thread.currentThread());
        mailConfig.setPort(8025)
                .setHostname("127.0.0.1")
                .setTrustAll(true)
                .setStarttls(StartTLSOptions.OPTIONAL)
                .setAuthMethods("LOGIN")
                .setUsername("testa@localhost")
                .setPassword("testa");
        MailClient client = MailClient.createShared(vertx, mailConfig);
        MailMessage message = new MailMessage();
        message
                .setText("中文")
                .setFrom("testa@localhost")
                .setTo("postmaster")
                .setSubject("Hello James to myself");
        client.sendMail(message, r -> {
            System.out.println("Sent Result at thread: " + Thread.currentThread());
            if (r.succeeded()) {
                System.out.println("Mail sent Result: " + r.result());
            } else {
                r.cause().printStackTrace();
            }
            vertx.close();
        });
    }
}
