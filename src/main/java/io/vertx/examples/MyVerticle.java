package io.vertx.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MyVerticle extends AbstractVerticle {

  private final static Logger logger = LoggerFactory.getLogger(MyVerticle.class);

  @Override
  public void start() {
    Router router = Router.router(vertx);
    router.get("/").handler(this::handleRequest);
    vertx.createHttpServer().requestHandler(router).listen(8080);
  }

  private void handleRequest(RoutingContext rc) {
    String msg = rc.request().getParam("msg");
    if (msg == null) {
      msg = "hello";
    }
    logger.info(String.format("Handle message: %s", msg));
    // just response as a start.
    rc.end(msg);
  }

}
