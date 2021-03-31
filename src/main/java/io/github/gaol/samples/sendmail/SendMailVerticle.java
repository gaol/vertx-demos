package io.github.gaol.samples.sendmail;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.StartTLSOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendMailVerticle extends AbstractVerticle {
  private MailClient mailClient;
  private final static Logger logger = Logger.getLogger("SendMailVerticle");

  // sent emails group by event loop
  private static final Map<Thread, AtomicInteger> sentMails = new ConcurrentHashMap<>();
  // start time
  private static long startTime;
  // total sent emails, count should be equals to sum of all values of sentMails map.
  private static final AtomicInteger totalSent = new AtomicInteger();

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
    vertx.eventBus().<JsonObject>consumer("mail.sent", m -> {
      JsonObject body = m.body();
      AtomicInteger total = new AtomicInteger(1);
      try {
        total.set(Integer.parseInt(body.getString("total", "1")));
      } catch (NumberFormatException ignored) {
      }
      MailMessage message = new MailMessage();
      message
        .setText(body.getString("content", "text email body"))
        .setFrom("testa@localtest.tld")
        .setTo("testb@localtest.tld")
      .setSubject(body.getString("subject", "test email subject"))
      ;
      final Thread t1 = Thread.currentThread();
      logger.info("Current Context outside of mailClient.sentMail(): " + Vertx.currentContext());
      mailClient.sendMail(message, r -> {
        logger.info("Current Context inside of mailClient.sentMail(): " + Vertx.currentContext());
        Thread t2 = Thread.currentThread();
        logger.info("t1: " + t1 + ", t2: " + t2);
        if (!t1.equals(t2)) {
          logger.info("Current Context Is Wrong: " + i);
          throw new IllegalStateException("context wrong !!!!!!!!!!!!!!!!!!!!!!!!");
        }
        if (r.succeeded()) {
          if (totalSent.get() == 0) {
            startTime = System.currentTimeMillis();
          }
          AtomicInteger sentCount = sentMails.computeIfAbsent(Thread.currentThread(), (k) -> new AtomicInteger(0));
          sentCount.incrementAndGet();
          totalSent.incrementAndGet();
          logger.info(totalSent.get() + " emails have been sent !!");
          if (totalSent.get() == total.get()) {
            // all sent, check statistics, print in server's log
            printStatistics(total.get());
          }
        } else {
          logger.log(Level.WARNING, "Failed to send email.", r.cause());
          r.cause().printStackTrace();
        }
      });
    });

  }

  private void printStatistics(int total) {
    StringBuilder sb = new StringBuilder("\n\t\tTotal Statistics\n");
    sb.append("Plan  sent  emails: \t").append(total).append("\n");
    sb.append("Actual sent emails: \t").append(totalSent.get()).append("\n");
    sb.append("\nSending in each event loop:\n");
    AtomicLong caclTotal = new AtomicLong(0L);
    sentMails.forEach((k, v) -> {
      sb.append(k).append("  :  \tsent ").append(v.get()).append(" emails.\n");
      caclTotal.addAndGet(v.get());
    });
    sb.append("Sent emails count(calculated): \t").append(caclTotal.get()).append("\n");
    sb.append("\n Time Consumed \n");
    sb.append("Start time: \t").append(timeString(startTime)).append("\n");
    long endTime = System.currentTimeMillis();
    sb.append("End time: \t").append(timeString(endTime)).append("\n");
    sb.append("Time elapsed: \t").append(timeLength(endTime - startTime));
    System.out.println(sb.toString());
    // clear at last
    clearStatistics();
  }

  private String timeLength(long time) {
    return (time / 1000) + " s";
  }

  private static final SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
  // time string on the milli-seconds
  private String timeString(long time) {
    return format.format(new Date(time));
  }

  @Override
  public void stop() throws Exception {
    mailClient.close();
  }

  static void clearStatistics() {
    sentMails.clear();
    totalSent.set(0);
    startTime = System.currentTimeMillis();
  }

}
