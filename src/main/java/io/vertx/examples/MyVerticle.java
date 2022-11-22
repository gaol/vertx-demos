package io.vertx.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MyVerticle extends AbstractVerticle {

    private final static Logger logger = LoggerFactory.getLogger(MyVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        router.get("/java").handler(this::handleRequest);
        vertx.createHttpServer().requestHandler(router).listen(8080)
                .onSuccess(s -> context.putLocal("httpServerPort", s.actualPort()))
                .flatMap(s -> vertx.deployVerticle("server.groovy"))
                .onComplete(r -> {
                    if (r.succeeded()) {
                        logger.info(String.format("Starts HttpServer from Java at: %d", (Integer)context.getLocal("httpServerPort")));
                        startPromise.complete();
                    } else {
                        r.cause().printStackTrace();
                        startPromise.fail(r.cause());
                    }
                });
    }

    private void handleRequest(RoutingContext rc) {
        rc.response().putHeader("Content-Type", "text/html")
                .end("<html><body><h1>Hello from vert.x in Java!</h1></body></html>");
    }

}
