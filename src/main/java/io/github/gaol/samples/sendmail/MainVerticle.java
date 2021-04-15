package io.github.gaol.samples.sendmail;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class MainVerticle extends AbstractVerticle {

  private Router router;

  static final String MAIL_SERVICE_ADDR = "send.email";

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    router = Router.router(vertx);
    router.route("/sendmail").handler(this::sendmail);
    router.route("/sendwait").handler(this::sendAndWait);
    vertx.deployVerticle(SendMailVerticle.class, new DeploymentOptions().setInstances(8))
            .flatMap(did -> vertx.createHttpServer().requestHandler(router).listen(8888))
            .flatMap(http -> startPromise.future());
  }

  private void sendmail(RoutingContext ctx) {
    vertx.eventBus().send(MAIL_SERVICE_ADDR, mailMessage(ctx));
    ctx.end("Sent!");
  }

  private void sendAndWait(RoutingContext ctx) {
    vertx.eventBus().request(MAIL_SERVICE_ADDR, mailMessage(ctx))
            .onComplete(r -> {
              if (r.succeeded()) {
                ctx.end(r.result().body().toString());
              } else {
                ctx.fail(r.cause());
              }
            });
  }

  private JsonObject mailMessage(RoutingContext ctx) {
    JsonObject eventBusMessage = new JsonObject();
    setField(eventBusMessage, "from", ctx);
    setField(eventBusMessage, "to", ctx);
    setField(eventBusMessage, "subject", ctx);
    setField(eventBusMessage, "content", ctx);
    return eventBusMessage;
  }

  private void setField(JsonObject json, String name, RoutingContext ctx) {
    if (ctx.request().getParam(name) != null) {
      json.put(name, ctx.request().getParam(name));
    }
  }

  @Override
  public void stop() throws Exception {
    router.clear();
  }

  public static void main(String[] args) {
    System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");
    Vertx.vertx().deployVerticle(new MainVerticle());
  }
}
