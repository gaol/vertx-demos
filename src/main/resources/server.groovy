import io.vertx.ext.web.Router

Router router = Router.router(vertx)
router.get("/groovy").handler(ctx -> {
    ctx.response().putHeader("Content-Type", "text/html")
            .end("<html><body><h1>Hello from vert.x in Groovy!</h1></body></html>")
})
vertx.createHttpServer().requestHandler(router).listen(8080).onComplete(r -> {
    if (r.succeeded()) {
        println "Starts HttpServer from Groovy at: " + r.result().actualPort()
    } else {
        r.cause().printStackTrace()
    }
});