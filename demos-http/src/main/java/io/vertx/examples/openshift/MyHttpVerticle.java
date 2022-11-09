package io.vertx.examples.openshift;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class MyHttpVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger("http-verticle");

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.get("/").handler(rc -> {
            String city = rc.request().getParam("city") == null ? "bj" : rc.request().getParam("city");
            vertx.eventBus().<JsonObject>request("city-house-price", city)
                    .onComplete(reply -> {
                        if (reply.failed()) {
                            rc.response().setStatusCode(400).end(reply.cause().getMessage());
                        } else {
                            rc.response().end(reply.result().body().encodePrettily());
                        }
                    });
        });
        router.get("/cities").handler(rc -> vertx.eventBus().<JsonObject>request("cities", null)
                .onComplete(reply -> {
                    if (reply.failed()) {
                        rc.response().setStatusCode(400).end(reply.cause().getMessage());
                    } else {
                        rc.response().end(reply.result().body().encodePrettily());
                    }
                }));

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080)
                .onSuccess(hs -> logger.info("Http server is listening on: " + hs.actualPort()))
                .<Void>mapEmpty()
                .onComplete(startPromise);
    }
}
