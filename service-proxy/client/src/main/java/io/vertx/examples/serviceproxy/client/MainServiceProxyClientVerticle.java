package io.vertx.examples.serviceproxy.client;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.examples.serviceproxy.DBService;
import io.vertx.examples.serviceproxy.DataEntry;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.util.stream.Collectors;

public class MainServiceProxyClientVerticle extends AbstractVerticle {

    private DBService dbService;

    private Router router;

    @Override
    public void start(Promise<Void> startPromise) {
        dbService = DBService.serviceProxy(vertx, "db.service");
        router = Router.router(vertx);
        router.route()
                .handler(BodyHandler.create())
                .handler(StaticHandler.create().setCachingEnabled(false));
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(Integer.getInteger("http.server", 8000))
                .onComplete(r -> {
                    if (r.succeeded()) {
                        System.out.printf("Starts HttpServer at: %d%n", r.result().actualPort());
                        startPromise.complete();
                    } else {
                        r.cause().printStackTrace();
                        startPromise.fail(r.cause());
                    }
                });
        router.get("/list").handler(this::list);
        router.post("/save/:name/").handler(this::save);
        router.post("/post").handler(this::save);

        // Allow events for the designated addresses in/out of the event bus bridge
        SockJSBridgeOptions opts = new SockJSBridgeOptions()
                .addInboundPermitted(new PermittedOptions()
                        .setAddress("db.service"))
                .addOutboundPermitted(new PermittedOptions()
                        .setAddress("db.service"));
        router.route("/eventbus/*").subRouter(SockJSHandler.create(vertx).bridge(opts));
    }

    @Override
    public void stop() throws Exception {
        router.clear();
    }

    private void list(RoutingContext ctx) {
        dbService.load().onComplete(result -> {
            if (result.succeeded()) {
                ctx.response().putHeader("Content-Type", "application/json");
                ctx.end(new JsonArray(result.result().stream()
                        .map(DataEntry::toJson).collect(Collectors.toList()))
                        .encodePrettily());
            } else {
                ctx.fail(500);
                result.cause().printStackTrace();
            }
        });
    }

    private void save(RoutingContext ctx) {
        String name = ctx.pathParam("name");
        // use body() to read post data as json
        String message = ctx.body().asJsonObject().getString("message");
        DataEntry entry = new DataEntry();
        entry
            .setName(name)
            .setMessage(message)
            .setId(System.currentTimeMillis());
        System.out.println("going to save EataEntry: " + entry);
        dbService.save(entry).onComplete(result -> {
            if (result.succeeded()) {
                ctx.end("Saved!");
            } else {
                ctx.fail(500);
                result.cause().printStackTrace();
            }
        });
    }

}
