package io.github.gaol.samples.sendmail;

import io.vertx.core.Future;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.mail.MailAttachment;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class SendMailTest extends VertxTestBase {

    private final static Logger logger = LoggerFactory.getLogger("SendMailTest");

    @Test
    public void testSendEmailWithAttachment(TestContext context) {
        String attachFilePath = "/home/lgao/Music/snow.mp3";
        String contentType = "audio/mpeg";
        final MailClient mailClient = SendMailVerticle.mailClient(vertx);
        final MailMessage message = new MailMessage()
                .setFrom("testa@localtest.tld")
                .setTo("testb@localtest.tld")
                .setSubject("please download the snow.mp3")
                .setText("There is a snow.mp3 in the attachment, it sounds great, enjoy it.");
        vertx.fileSystem().open(attachFilePath, new OpenOptions())
                .flatMap(af -> Future.succeededFuture(MailAttachment.create().setStream(af).setContentType(contentType)
                        .setName("Snow Song")))
                .flatMap(attachment -> mailClient.sendMail(message.setAttachment(attachment)))
                .onComplete(context.asyncAssertSuccess(mr -> {
                    mailClient.close();
                    logger.info(mr.toJson());
                }));
    }

    @Test
    public void testSendEmailWithInAttachment(TestContext context) {
        String attachFilePath = "/home/lgao/NetworkOptions.png";
        String contentType = "image/png";
        final MailClient mailClient = SendMailVerticle.mailClient(vertx);
        final MailMessage message = new MailMessage()
                .setFrom("testa@localtest.tld")
                .setTo("testb@localtest.tld")
                .setSubject("Network Options in Vert.x")
                .setHtml("<html><body><h3>Please take look at the NetworkOptions.png for the options</h3>" +
                        "<img width=\"640px\" height=\"480px\" src=\"cid:NetworkOptions.png\">\n" +
                        "</img></body></html>");
        vertx.fileSystem().open(attachFilePath, new OpenOptions())
                .flatMap(af -> Future.succeededFuture(MailAttachment.create().setStream(af).setContentType(contentType)
                        .setContentId("<NetworkOptions.png>")
                        .setDisposition("inline")))
                .flatMap(attachment -> mailClient.sendMail(message.setInlineAttachment(attachment)))
                .onComplete(context.asyncAssertSuccess(mr -> {
                    mailClient.close();
                    logger.info(mr.toJson());
                }));
    }

}
