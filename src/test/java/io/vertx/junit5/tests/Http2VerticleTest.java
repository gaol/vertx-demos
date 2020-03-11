package io.vertx.junit5.tests;


import io.vertx.core.Vertx;
import io.vertx.demos.Http2Verticle;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class Http2VerticleTest {

    private Vertx vertx;

    @Test
    @DisplayName("Test http request a")
    void http2RequestA(final VertxTestContext testContext) {
        WebClient.create(vertx).get(8080, "localhost", "/").send(resp -> {
//            System.out.println("Get ResponseA: " + resp.result().body().toString() + ", at thread: " + Thread.currentThread());
            testContext.completeNow();
        });

    }

    @Test
    @DisplayName("Test http request b")
    void http2RequestB(final VertxTestContext testContext) {
        WebClient.create(vertx).get(8080, "localhost", "/").send(resp -> {
//            System.out.println("Get ResponseB: " + resp.result().body().toString() + ", at thread: " + Thread.currentThread());
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            testContext.completeNow();
        });

    }

    @BeforeEach
    public void before(final VertxTestContext testContext) {
        System.out.println("Start vertx " + Thread.currentThread());
        vertx = Vertx.vertx();
        vertx.deployVerticle(new Http2Verticle(),
                testContext.completing());
    }
    @AfterEach
    public void cleanUp(final VertxTestContext testContext) {
        vertx.close(testContext.completing());
    }

}
