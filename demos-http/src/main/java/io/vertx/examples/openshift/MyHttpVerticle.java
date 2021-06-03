package io.vertx.examples.openshift;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;

public class MyHttpVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);
      HealthCheckHandler healthCheckHandler = HealthCheckHandler.create(vertx)
              .register("server-online", fut -> fut.complete(Status.OK()));
    router.get("/").handler(rc -> rc.response().end("OK"));
    router.get("/readiness").handler(rc -> rc.response().end("OK"));
    router.get("/liveness").handler(healthCheckHandler);
    router.get("/price").handler(rc -> {
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
            .onSuccess(hs -> System.out.println("Http server is listening on: " + hs.actualPort()))
            .<Void>mapEmpty()
        .onComplete(startPromise);
  }
}
