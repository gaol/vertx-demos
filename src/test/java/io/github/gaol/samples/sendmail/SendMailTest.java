package io.github.gaol.samples.sendmail;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class SendMailTest extends VertxTestBase {

    private final static Logger logger = LoggerFactory.getLogger("SendMailTest");

    private final static int WAIT_TO_CLOSE = 1000 * 30;

    // https://github.com/vert-x3/vertx-mail-client/issues/175
    @Test
    public void testSendEmailOffice365WithoutClose(TestContext context) {
        Async async = context.async();
        EmailVendor vendor = EmailVendor.OFFICE365;
        SMTPAware smtpAware = new SMTPAware(vertx);
        MailConfig mailConfig = smtpAware.mailConfig(vendor);
        MailClient mailClient = MailClient.create(vertx, mailConfig);
        mailClient.sendMail(smtpAware.emailMessage(vendor)).onComplete(ar -> {
            logger.info(ar.result());
            vertx.setTimer(WAIT_TO_CLOSE, l -> {
                logger.info("Countdown after sometime waiting");
                mailClient.close();
                async.countDown();
            });
        });
        async.await();
    }

    @Test
    public void testSendEmailGmailWithoutClose(TestContext context) {
        Async async = context.async();
        EmailVendor vendor = EmailVendor.GMAIL;
        SMTPAware smtpAware = new SMTPAware(vertx);
        MailConfig mailConfig = smtpAware.mailConfig(vendor);
        MailClient mailClient = MailClient.create(vertx, mailConfig);
        mailClient.sendMail(smtpAware.emailMessage(vendor)).onComplete(ar -> {
            logger.info(ar.result());
            vertx.setTimer(WAIT_TO_CLOSE, l -> {
                logger.info("Countdown after sometime waiting");
                mailClient.close();
                async.countDown();
            });
        });
        async.await();
    }

    @Test
    public void testSendEmail163WithoutClose(TestContext context) {
        Async async = context.async();
        EmailVendor vendor = EmailVendor.MAIL_163;
        SMTPAware smtpAware = new SMTPAware(vertx);
        MailConfig mailConfig = smtpAware.mailConfig(vendor);
        MailClient mailClient = MailClient.create(vertx, mailConfig);
        mailClient.sendMail(smtpAware.emailMessage(vendor)).onComplete(ar -> {
            logger.info(ar.result());
            vertx.setTimer(WAIT_TO_CLOSE, l -> {
                logger.info("Countdown after sometime waiting");
                mailClient.close();
                async.countDown();
            });
        });
        async.await();
    }

    @Test
    public void testSendLocalTestTldWithoutClose(TestContext context) {
        Async async = context.async();
        EmailVendor vendor = EmailVendor.LOCAL_TEST_TLD;
        SMTPAware smtpAware = new SMTPAware(vertx);
        MailConfig mailConfig = smtpAware.mailConfig(vendor);
        MailClient mailClient = MailClient.create(vertx, mailConfig);
        mailClient.sendMail(smtpAware.emailMessage(vendor)).onComplete(ar -> {
            logger.info(ar.result());
            vertx.setTimer(WAIT_TO_CLOSE, l -> {
                logger.info("Countdown after sometime waiting");
                mailClient.close();
                async.countDown();
            });
        });
        async.await();
    }

}
