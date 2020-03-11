package io.vertx.demos;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public class VertxStartTime {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        Vertx vertx = Vertx.vertx();
        EventBus eb = null;
        eb = vertx.eventBus();

        long end = System.currentTimeMillis();
        System.out.println("total time = " + (end - start));

        start = System.currentTimeMillis();
        vertx = Vertx.vertx();
        eb = vertx.eventBus();
        end = System.currentTimeMillis();
        System.out.println("total time = " + (end - start));
    }
}
