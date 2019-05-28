package io.vertx.examples;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

@RunWith(VertxUnitRunner.class)
public class SampleHttpServerTest {
  private Vertx vertx;

  @Before
  public void setup() {
    vertx = Vertx.vertx();
  }

  @After
  public void teardown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void checkCityDefault(TestContext context) {
    Async async = context.async();
    vertx.deployVerticle(new MyVerticle(), context.asyncAssertSuccess(s -> {
      WebClient webClient = WebClient.create(vertx);
        webClient.get(8080, "localhost", "/").send(ar -> {
          if (ar.succeeded()) {
            HttpResponse<Buffer> response = ar.result();
            context.assertEquals(200, response.statusCode());
            context.assertTrue(response.headers().contains("Content-Type"));
            context.assertEquals("application/json", response.getHeader("Content-Type"));
            // {"Average":"64293 CNY/SQM","Latest":"62704 CNY/SQM"}
            try {
              JsonObject json = response.bodyAsJsonObject();
              long average = Long.parseLong(json.getString("Average").split(" ")[0]);
              long latest = Long.parseLong(json.getString("Latest").split(" ")[0]);

              // It will fail once House price is lower than 60000 CNY/SQM in Beijing
              context.assertTrue(average > 60000);
              context.assertTrue(latest > 60000);
              // async.complete();
              async.countDown();
            } catch (Exception e) {
              async.resolve(Future.failedFuture(e));
            }
          } else {
            async.resolve(Future.failedFuture(ar.cause()));
          }
        });
    }));
  }

}