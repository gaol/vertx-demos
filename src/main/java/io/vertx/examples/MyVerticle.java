package io.vertx.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;

public class MyVerticle extends AbstractVerticle {

  HttpRequest<JsonObject> request;

  private static int i = 0;

  private int index;

  @Override
  public void start() {
    index = ++i;
    System.out.println("Instance of: " + index + " Started at thread: @ " + Thread.currentThread());
    this.request = WebClient.create(vertx)
            .get(443, "icanhazdadjoke.com", "/")
            .ssl(true)
            .putHeader("Accept", "application/json")
            .as(BodyCodec.jsonObject())
            .expect(ResponsePredicate.SC_OK)
    ;

    vertx.setPeriodic(3000, id -> fetchJoke());
  }

  private void fetchJoke() {
    System.out.println("Instance of: " + index + " Fetching Joke at thread: @ " + Thread.currentThread());
    request.send(asyncResult -> {
      System.out.println("Instance of: " + index + " Fetched Joke at thread: @ " + Thread.currentThread());
      if (asyncResult.succeeded()) {
        System.out.println(asyncResult.result().body().getString("joke")); // (7)
        System.out.println();
      }
    });
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    DeploymentOptions deployOptions = new DeploymentOptions().setInstances(5);
    vertx.deployVerticle(MyVerticle.class.getName(), deployOptions);
  }

}
