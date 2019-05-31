package io.vertx.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

public class MyVerticle extends AbstractVerticle {

  private final static Logger logger = LoggerFactory.getLogger(MyVerticle.class);

  @Override
  public void start() {
    Router router = Router.router(vertx);
    router.get("/").handler(this::handleRequest);
    vertx.createHttpServer().requestHandler(router).listen(8080);
  }

  private void handleRequest(RoutingContext rc) {
    String city = rc.request().getParam("city");
    if (city == null) {
      city = "bj";
    }
    final String theCity = city;
    WebClient webClient = WebClient.create(vertx);
    cityHouseRegionIdRequest(webClient, theCity).send(ar -> {
      if (ar.succeeded()) {
        String regionId = cityRegionId(ar.result());
        cityHousePriceRequest(webClient, theCity, regionId).send(arr -> {
          if (arr.succeeded()) {
            JsonObject content = cityHousePrice(arr.result());
            logger.info(String.format("Got house price of city: %s is: %s", theCity, content.toString()));
            rc.response().putHeader("Content-Type", "application/json").end(content.toBuffer());
          } else {
            logger.error("Failed to get house price of city: " + theCity, arr.cause());
            rc.response().setStatusCode(400).end(arr.cause().getMessage());
          }
        });
      } else {
        logger.error("Failed to get regionId of city: " + theCity, ar.cause());
        rc.response().setStatusCode(400).end(ar.cause().getMessage());
      }
    });
  }

  private static HttpRequest<Buffer> cityHouseRegionIdRequest(WebClient webClient, String city) {
    return webClient.get(443, String.format("%s.lianjia.com", city), "/").ssl(true);
  }

  private static HttpRequest<Buffer> cityHousePriceRequest(WebClient webClient, String city, String regionId) {
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
    for (String line : lines) {
      if (line.trim().startsWith("ljweb_cid:")) {
        return line.split("'")[1];
      }
    }
    throw new RuntimeException("Cannot get regionId");
  }

}
