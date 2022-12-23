package io.vertx.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public class MyVerticle extends AbstractVerticle {

    public static final String ADDRESS = "to-address";

    @Override
    public void start(Promise<Void> startPromise) {
        System.out.println("\n  =======  Starting A HTTP Server to support SSE on eventbus address: " + ADDRESS + "  ======= \n");
        vertx.createHttpServer()
                .requestHandler(sse())
                .listen(Integer.getInteger("http.server.port", 9080))
                .onSuccess(hs -> {
                  System.out.println("\n  === HTTP Server started at: " + hs.actualPort() + " ==== \n");
                  startPromise.complete();
                })
                .onFailure(startPromise::fail);
    }

  private Handler<HttpServerRequest> sse() {
    return req -> {
      HttpServerResponse response = req.response();
      response.putHeader("Content-Type", "text/event-stream")
              .putHeader("Cache-Control", "no-cache")
              .setChunked(true);
      MessageConsumer<String> consumer = vertx.eventBus().consumer(ADDRESS);
      consumer.handler(msg -> response.write("data: " + msg.body() + "\n\n"));
      response.endHandler(v -> consumer.unregister().onComplete(u -> System.out.println("\n==== Consumer gets unregistered. ==== \n")));
    };
  }

}
