package io.vertx.examples.polyglot;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MainPolyglotVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) {
        vertx.deployVerticle("server.groovy")
                .flatMap(s -> vertx.deployVerticle("server.js"))
                .flatMap(s -> vertx.deployVerticle("io.vertx.examples.polyglot.Server"))
                .flatMap(s -> vertx.createHttpServer()
                        .requestHandler(r -> r.response()
                                .putHeader("Content-Type", "text/html")
                                .end("<html><body><h1>Hello from vert.x in Java!</h1></body></html>"))
                        .listen(8000))
                .onComplete(r -> {
                    if (r.succeeded()) {
                        System.out.printf("Starts HttpServer from Java at: %d%n", r.result().actualPort());
                        startPromise.complete();
                    } else {
                        r.cause().printStackTrace();
                        startPromise.fail(r.cause());
                    }
                });
    }

}
