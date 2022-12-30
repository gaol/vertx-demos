package io.vertx.examples.serviceproxy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.serviceproxy.ServiceBinder;

public class MainServiceProxyVerticle extends AbstractVerticle {

    private DBService dbService;

    @Override
    public void start() {
        dbService = DBService.createService(vertx);
        new ServiceBinder(vertx)
                .setAddress("db.service")
                .register(DBService.class, dbService);
        System.out.println("\n\n ===   DBService registered   === \n\n");
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        dbService.close().onComplete(stopPromise);
    }
}
