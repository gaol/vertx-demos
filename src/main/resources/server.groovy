vertx.createHttpServer().requestHandler(r -> r.response()
        .putHeader("Content-Type", "text/html")
        .end("<html><body><h1>Hello from vert.x in Groovy!</h1></body></html>"))
        .listen(8001)
        .onComplete(r -> {
            if (r.succeeded()) {
                println "Starts HttpServer from Groovy at: " + r.result().actualPort()
            } else {
                r.cause().printStackTrace()
            }
        });