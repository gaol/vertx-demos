package io.vertx.examples.openshift;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

public class MyServiceVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    final WebClient webClient = WebClient.create(vertx);
    vertx.eventBus().<String>consumer("city-house-price")
        .handler(msg -> webClient.get(443, String.format("%s.lianjia.com", msg.body()), "/").send()
            .flatMap(resp -> webClient.get(443, String.format("%s.lianjia.com", msg.body()), reqPath(resp)).send())
            .onComplete(asyncResult -> {
              if (asyncResult.succeeded()) {
                msg.reply(cityHousePrice(asyncResult.result().bodyAsJsonObject()
                        .getJsonObject("currentLevel")
                        .getJsonObject("dealPrice")
                        .getJsonArray("total")));
              } else {
                msg.fail(500, asyncResult.cause().getMessage());
              }
            }));

  }

  // ========================= STATIC UTILS METHODS =============================
  private static String reqPath(HttpResponse<Buffer> response) {
    String regionId = cityRegionId(response.body().toString().split("\n"));
    return String.format("/fangjia/priceTrend/?region=city&region_id=%s", regionId);
  }

  private static JsonObject cityHousePrice(JsonArray total) {
    long average = Math.round(total.stream().mapToInt(ln -> (Integer) ln).average().orElse(0.0d));
    int latest = total.stream().mapToInt(ln -> (Integer) ln).skip(total.size() - 1).findFirst().orElse(0);
    return new JsonObject().put("Average", average + " CNY/SQM").put("Latest", latest + " CNY/SQM");
  }

  private static String cityRegionId(String[] lines) {
    for (String line: lines) {
      if (line.trim().startsWith("ljweb_cid:")) {
        return line.split("'")[1];
      }
    }
    throw new RuntimeException("Cannot get regionId");
  }

}
