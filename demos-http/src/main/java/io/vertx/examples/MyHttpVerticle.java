package io.vertx.examples;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;

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
      vertx.eventBus()
        .<JsonObject>rxSend("city-house-price", city)
        .map(m -> m.body().toBuffer())
        .subscribe(s -> {
            logger.info(String.format("\nHouse price of city: %s is: %s", theCity, s));
          rc.response().getDelegate().end(s);
        }, e -> {
          logger.error("Failed to query the house price.", e);
          rc.response().setStatusCode(400).end(e.getMessage());
        });
    });

    vertx.createHttpServer()
        .requestHandler(router)
        .listen(8080);

  }
}
