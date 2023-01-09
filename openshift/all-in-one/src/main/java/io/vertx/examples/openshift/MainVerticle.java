package io.vertx.examples.openshift;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) {
      vertx.deployVerticle(new HttpVerticle())
              .flatMap(d -> vertx.deployVerticle(new ServiceVerticle()))
              .<Void>mapEmpty()
              .onComplete(startPromise);
  }
}
