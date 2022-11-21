package io.vertx.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import static io.vertx.examples.ServiceVerticle.SERVICE_ADDRESS;

public class HttpVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger("http-verticle");

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.get("/").handler(rc -> {
            String param = rc.request().getParam("param") == null ? "status" : rc.request().getParam("param");
            vertx.eventBus().<JsonObject>request(SERVICE_ADDRESS, param)
                    .onComplete(reply -> {
                        if (reply.failed()) {
                            rc.response().setStatusCode(400).end(reply.cause().getMessage());
                            reply.cause().printStackTrace();
                        } else {
                            rc.response().end(reply.result().body().encodePrettily());
                        }
                    });
        });
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080)
                .onSuccess(hs -> logger.info("Http server is listening on: " + hs.actualPort()))
                .<Void>mapEmpty()
                .onComplete(startPromise);
    }

}
