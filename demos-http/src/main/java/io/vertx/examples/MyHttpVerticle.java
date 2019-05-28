package io.vertx.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

public class MyHttpVerticle extends AbstractVerticle {

  private final static Logger logger = LoggerFactory.getLogger(MyHttpVerticle.class);

  @Override
  public void start() {
    Router router = Router.router(vertx);
    router.get("/").handler(rc -> {
      String city = rc.request().getParam("city");
      if (city == null) {
        city = "bj";
      }
      final String theCity = city;
      vertx.eventBus().<JsonObject>send("city-house-price", theCity, reply -> {
        if (reply.failed()) {
          logger.error("Failed to query the house price.", reply.cause());
          rc.response().setStatusCode(400).end(reply.cause().getMessage());
        } else {
          JsonObject content = reply.result().body();
          logger.info(String.format("\nGot house price of city: %s is: %s", theCity, content.toString()));
          rc.response().end(content.toBuffer());
        }
      });
    });

    vertx.createHttpServer()
        .requestHandler(router)
        .listen(8080);

  }
}
