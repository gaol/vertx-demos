package io.vertx.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.healthchecks.Status;

public class ServiceVerticle extends AbstractVerticle {

  static final String SERVICE_ADDRESS = "health";

  @Override
  public void start() {
    HealthChecks healthChecks = HealthChecks.create(vertx);
    healthChecks.register("test", promise -> promise.complete(Status.OK().setData(new JsonObject().put("value", "123"))));
    vertx.eventBus().consumer(SERVICE_ADDRESS,
            message -> healthChecks.invoke(message::reply));

  }

}
