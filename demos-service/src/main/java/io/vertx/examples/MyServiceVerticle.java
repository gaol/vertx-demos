package io.vertx.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

public class MyServiceVerticle extends AbstractVerticle {

  private final static Logger logger = LoggerFactory.getLogger(MyServiceVerticle.class);

  @Override
  public void start() {
    vertx.eventBus().<String>consumer("city-house-price", msg -> {
      WebClient webClient = WebClient.create(vertx);
      webClient.get(443, String.format("%s.lianjia.com", msg.body()), "/")
        .ssl(true)
        .send(ar -> {
          if (ar.succeeded()) {
            String regionId = cityRegionId(ar.result());
            webClient.get(443, String.format("%s.lianjia.com", msg.body()), String.format("/fangjia/priceTrend/?region=city&region_id=%s", regionId))
              .ssl(true)
              .send(arr -> {
                if (arr.succeeded()) {
                  logger.info(String.format("\nChecking house price of city: %s", msg.body()));
                  msg.reply(cityHousePrice(arr.result()));
                } else {
                  logger.error("Failed to get house price of city: " + msg.body(), arr.cause());
                  msg.fail(200, arr.cause().getMessage());
                }
              });
          } else {
            logger.error("Failed to get regionId of city: " + msg.body(), ar.cause());
            msg.fail(100, ar.cause().getMessage());
          }
        });
    });
  }

  private static JsonObject cityHousePrice(HttpResponse<Buffer> resp) {
    JsonArray ja = resp.bodyAsJsonObject().getJsonObject("currentLevel").getJsonObject("dealPrice")
        .getJsonArray("total");
    long average = Math.round(ja.stream().mapToInt(ln -> (Integer) ln).average().orElse(0.0d));
    int latest = ja.stream().mapToInt(ln -> (Integer) ln).skip(ja.size() - 1).findFirst().orElse(0);
    return new JsonObject().put("Average", average + " CNY/SQM").put("Latest", latest + " CNY/SQM");
  }

  private static String cityRegionId(HttpResponse<Buffer> resp) {
    String[] lines = resp.body().toString().split("\n");
    for (String line: lines) {
      if (line.trim().startsWith("ljweb_cid:")) {
        return line.split("'")[1];
      }
    }
    throw new RuntimeException("Cannot get regionId");
  }

}
