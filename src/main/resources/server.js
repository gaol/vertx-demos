vertx.createHttpServer().requestHandler(function (req) {
    req.response()
        .putHeader("Content-Type", "text/html")
        .end("<html><body><h1>Hello from vert.x in JavaScript!</h1></body></html>");
    })
    .listen(8003)
    .onComplete(function (r) {
         console.log("Starts HttpServer from JavaScript at: " + r.result().actualPort());
    });