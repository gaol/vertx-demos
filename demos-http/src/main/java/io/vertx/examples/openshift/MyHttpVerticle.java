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
            logger.info("Hold on, about to request house price of city: " + city);
            vertx.eventBus().<JsonObject>request("city-house-price", city)
                    .onComplete(reply -> {
                        if (reply.failed()) {
                            logger.error("Sorry, I can't know the house price of city: " + city, reply.cause());
                            rc.response().setStatusCode(400).end(reply.cause().getMessage());
                        } else {
                            JsonObject content = reply.result().body();
                            logger.info("Here is the house price of city " + city + ": " + content.encodePrettily());
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
