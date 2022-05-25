package io.vertx.examples.openshift;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

public class MyHttpVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.get("/").handler(rc -> {
            String message = rc.request().getParam("message") == null ? "Hi" : rc.request().getParam("message");
            vertx.eventBus().<String>request("echo", message)
                    .onComplete(reply -> {
                        if (reply.failed()) {
                            rc.response().setStatusCode(500).end(reply.cause().getMessage());
                        } else {
                            rc.response().end(reply.result().body());
                        }
                    });
        });

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8888)
                .onSuccess(hs -> System.out.println("\n\tHttp server is listening on: " + hs.actualPort()))
                .<Void>mapEmpty()
                .onComplete(startPromise);
    }
}
