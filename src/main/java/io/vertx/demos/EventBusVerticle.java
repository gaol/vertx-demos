package io.vertx.demos;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

public class EventBusVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.eventBus().localConsumer("local-address", msg -> {
           System.out.println("Message is: " + msg.body());
        });
        MyPojo pojo = new MyPojo();
        pojo.setId(100L);
        pojo.setName("my name");
        vertx.eventBus().send("local-address", pojo);
    }

    private class MyPojo {
        private long id;
        private String name;

        public long getId() {
            return id;
        }

        public MyPojo setId(long id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public MyPojo setName(String name) {
            this.name = name;
            return this;
        }

        @Override
        public String toString() {
            return "[id: " + id + ", name: " + name + "]";
        }
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new EventBusVerticle());
    }
}
