package io.vertx.examples.openshift;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class MyHttpVerticle extends AbstractVerticle {

  @Override
  public void start() {
    Router router = Router.router(vertx);
    router.get("/").handler(rc -> {
      String city = rc.request().getParam("city");
      if (city == null) {
        city = "bj";
      }
      final String theCity = city;
      vertx.eventBus().<JsonObject>request("city-house-price", theCity)
              .onComplete(reply -> {
                  if (reply.failed()) {
                    rc.response().setStatusCode(400).end(reply.cause().getMessage());
                  } else {
                    JsonObject content = reply.result().body();
                    rc.response().end(content.toBuffer());
                  }
              });
    });

    vertx.createHttpServer()
        .requestHandler(router)
        .listen(8080)
        .onComplete(hs -> System.out.println("Http server is listening on: " + hs.result().actualPort()));
  }
}
