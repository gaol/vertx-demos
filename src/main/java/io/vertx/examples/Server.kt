package io.vertx.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise

class Server : AbstractVerticle() {
    override fun start(startFuture: Promise<Void>) {
        vertx.createHttpServer()
            .requestHandler { r ->
                r.response()
                    .putHeader("Content-Type", "text/html")
                    .end("<html><body><h1>Hello from vert.x in Kotlin!</h1></body></html>")
            }
            .listen(8002) { result ->
                if (result.succeeded()) {
                    println("Starts HttpServer from Kotlin at: " + result.result().actualPort())
                    startFuture.complete()
                } else {
                    startFuture.fail(result.cause())
                }
            }
    }

}
