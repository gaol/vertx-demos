package io.vertx.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MyVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        vertx.createHttpServer().requestHandler(r -> r.response()
                        .putHeader("Content-Type", "text/html")
                        .end("<html><body><h1>Hello from vert.x in Java!</h1></body></html>"))
                .listen(8000)
                .onSuccess(s -> context.putLocal("httpServerPort", s.actualPort()))
                .flatMap(s -> vertx.deployVerticle("server.groovy"))
                .flatMap(s -> vertx.deployVerticle("server.js"))
                .flatMap(d -> vertx.deployVerticle("io.vertx.examples.Server"))
                .onComplete(r -> {
                    if (r.succeeded()) {
                        System.out.printf("Starts HttpServer from Java at: %d%n", (Integer) context.getLocal("httpServerPort"));
                        startPromise.complete();
                    } else {
                        r.cause().printStackTrace();
                        startPromise.fail(r.cause());
                    }
                });
    }

}
