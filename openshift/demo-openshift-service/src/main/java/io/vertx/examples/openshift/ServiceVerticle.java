package io.vertx.examples.openshift;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class ServiceVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger("service-verticle");

  private final FakeData data = new FakeData();

  @Override
  public void start() {
    vertx.eventBus().<String>consumer("city-house-price")
        .handler(msg -> msg.reply(data.getPrice(msg.body())));
    vertx.eventBus().<String>consumer("cities")
            .handler(msg -> msg.reply(data.cities()));
    logger.info("Listening on address: city-house-price to response house price of any city.");
  }

}
