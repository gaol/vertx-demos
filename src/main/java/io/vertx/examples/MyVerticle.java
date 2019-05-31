package io.vertx.examples;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;

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
    cityHouseRegionIdRequest(webClient, theCity).rxSend().map(MyVerticle::cityRegionId)
        .flatMap(regionId -> cityHousePriceRequest(webClient, theCity, regionId).rxSend())
        .map(MyVerticle::cityHousePrice).map(JsonObject::toBuffer).subscribe(s -> {
          logger.info(String.format("House price of %s is: %s", theCity, s.toString()));
          rc.response().getDelegate().putHeader("Content-Type", "application/json").end(s);
        }, e -> {
          logger.error(String.format("Failed to get house price of city: %s", theCity), e);
          rc.response().setStatusCode(400).end(e.getMessage());
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
