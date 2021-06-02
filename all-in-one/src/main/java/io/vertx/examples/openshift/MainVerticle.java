package io.vertx.examples.openshift;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
      vertx.deployVerticle(new MyHttpVerticle())
              .flatMap(d -> vertx.deployVerticle(new MyServiceVerticle()))
              .<Void>mapEmpty()
              .onComplete(startPromise);
  }
}
