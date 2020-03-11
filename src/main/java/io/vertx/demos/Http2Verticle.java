package io.vertx.demos;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.SelfSignedCertificate;

public class Http2Verticle extends AbstractVerticle {

  @Override
  public void start() {
    SelfSignedCertificate certificate = SelfSignedCertificate.create();
    HttpServerOptions httpServerOptions = new HttpServerOptions()
            .setUseAlpn(true)
            .setSsl(true)
            .setKeyCertOptions(certificate.keyCertOptions());
    HttpServer httpServer = vertx.createHttpServer(httpServerOptions);
    httpServer.requestHandler(req -> {
      System.out.println("HTTP Version: " + req.version());
      req.headers().forEach(entry -> {
        System.out.println(entry);
      });
      String msg = "Hello from Http/2";
      req.response().putHeader("Content-Length", "" + msg.length()).end(msg);
    });
    httpServer.listen(8080);
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new Http2Verticle());
  }
}
