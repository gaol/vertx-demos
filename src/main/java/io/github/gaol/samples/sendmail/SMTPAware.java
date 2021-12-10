package io.github.gaol.samples.sendmail;

import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;

class SMTPAware {

    private static final Logger log = LoggerFactory.getLogger(SMTPAware.class);

    private final JsonObject config;

    SMTPAware(Vertx vertx) {
        String configFile = System.getProperty("smtp.config", System.getProperty("user.home") + "/.smtp.json");
        log.info("Read configuration from " + configFile);
        this.config = new JsonObject(vertx.fileSystem().readFileBlocking(configFile));
    }

    MailMessage emailMessage(EmailVendor vendor) {
        return new MailMessage(config.getJsonObject(vendor.name().toLowerCase()).getJsonObject("email"));
    }

    MailConfig mailConfig(EmailVendor vendor) {
        return new MailConfig(config.getJsonObject(vendor.name().toLowerCase()).getJsonObject("smtp"));
    }

}
