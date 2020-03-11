package io.vertx.demos;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.StartTLSOptions;

public class SendMultipleMails {
    public static void main(String[] args) throws Exception {

        Vertx vertx = Vertx.vertx();
        // 10 instances for each SendMailVerticle
        vertx.deployVerticle(SendMailVerticle.class, new DeploymentOptions().setInstances(8));
    }
}
