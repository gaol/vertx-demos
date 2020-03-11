package io.vertx.junit5.tests;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.concurrent.TimeUnit;

@ExtendWith(VertxExtension.class)
public class WorkVerticleTests {

    private class MainVerticle extends AbstractVerticle {
        @Override
        public void start() throws Exception {
//            vertx.deployVerticle(new EventBusVerticle(), new DeploymentOptions().setWorker(true));
        }
    }

    @Test
    @Timeout(value = 155, timeUnit = TimeUnit.SECONDS)
    void testUpdateAccount(Vertx pVertx, VertxTestContext pTestContext) {
        MainVerticle mainVerticle = new MainVerticle();
        pTestContext.assertComplete(pVertx.deployVerticle(mainVerticle))
                .onSuccess(t -> {
                    System.out.println("Deployed: " + t);
                    pTestContext.assertComplete(pVertx.fileSystem().copy(
                            "src/test/resources/test-config.properties",
                            "target/test-output.properties"))
                            .onSuccess(f -> {
                                System.out.println("Copied");
                                pTestContext.verify(() -> {
                                    pVertx.fileSystem().readFile("target/test-output.properties").onSuccess(b -> {
                                        System.out.println("Buffer from output: " + b.toString());
//                                        pTestContext.completeNow();
                                    });
                                });
                            });
                });
    }

}
