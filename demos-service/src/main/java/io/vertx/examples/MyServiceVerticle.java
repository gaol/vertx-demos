package io.vertx.examples;

import java.util.concurrent.atomic.AtomicReference;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;

public class MyServiceVerticle extends AbstractVerticle {

  private final static Logger logger = LoggerFactory.getLogger(MyServiceVerticle.class);

  @Override
  public void start() {
    WebClient webClient = WebClient.create(vertx);
    vertx.eventBus().<String>consumer("city-house-price", msg -> {
      String city = msg.body();
      cityHouseRegionIdRequest(webClient, city)
        .rxSend()
        .map(MyServiceVerticle::cityRegionId)
        .flatMap(regionId -> cityHousePriceRequest(webClient, msg.body(), regionId).rxSend())
        .map(MyServiceVerticle::cityHousePrice)
        .subscribe(s -> {
          logger.info(String.format("\nChecking house price of city: %s", s));
          msg.reply(s);
        }, e -> {
            logger.error("Failed to get regionId of city: " + city, e);
            msg.fail(100, e.getMessage());
        });
    });
  }


  // Helper methods below
  private static HttpRequest<Buffer> cityHouseRegionIdRequest(WebClient webClient, String city) {
    return webClient.get(443, String.format("%s.lianjia.com", city), "/").ssl(true);
  }

  private static HttpRequest<Buffer> cityHousePriceRequest(WebClient webClient,String city, String regionId) {
    return webClient.get(443, String.format("%s.lianjia.com", city),
            String.format("/fangjia/priceTrend/?region=city&region_id=%s", regionId)).ssl(true);
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
