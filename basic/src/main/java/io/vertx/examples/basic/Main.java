package io.vertx.examples.basic;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    // count does not need to be synchronized
    private int count = 0;

    @Override
    public void start() {
        logger.info("Start the Verticle.");
        vertx.createHttpServer().requestHandler(req -> {
            if (req.path().equals("/count")) {
                logger.info("Send count back.");
                req.response()
                        .putHeader("content-type", "text/plain")
                        .end("" + count);
            } else {
                logger.info("increase count by 1: " + ++count);
                req.response()
                        .putHeader("content-type", "text/plain")
                        .end("Hello From Vertx !");
            }
        }).listen(8080).onSuccess(s -> logger.info("HttpServer started at port: " + s.actualPort()));
    }

    public static void main(String[] args) {
        logger.info("Start the main");
        Vertx vertx = Vertx.vertx();
        // deploy 1 instance
        vertx.deployVerticle(new Main());
    }
}
