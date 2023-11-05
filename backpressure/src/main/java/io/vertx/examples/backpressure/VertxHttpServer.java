/*
 *  Copyright (c) 2023 The original author or authors
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of Apache License v2.0 which
 *  accompanies this distribution.
 *
 *       The Apache License v2.0 is available at
 *       http://www.opensource.org/licenses/apache2.0.php
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.vertx.examples.backpressure;

import io.undertow.util.Headers;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.vertx.examples.backpressure.Main.MESSAGE_ADDR;

/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
public class VertxHttpServer extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger("VertxHttpServer");

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Router router = Router.router(vertx);
        router.route()
                .handler(BodyHandler.create())
                .handler(StaticHandler.create().setCachingEnabled(false));
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        SockJSBridgeOptions options = new SockJSBridgeOptions()
                .addOutboundPermitted(new PermittedOptions().setAddress(MESSAGE_ADDR));
        router.route("/eventbus/*").subRouter(sockJSHandler.bridge(options));
        router.route().handler(rc -> {
            rc.response().putHeader(Headers.CACHE_CONTROL_STRING, "no-cache");
            rc.next();
        });
        router.route().handler(StaticHandler.create().setCachingEnabled(false));
        vertx.createHttpServer()
                .requestHandler(router)
                .listen(Integer.getInteger("http.server", 8000))
                .onComplete(r -> {
                    if (r.succeeded()) {
                        logger.info(String.format("Starts HttpServer using Vert.x at: %d", r.result().actualPort()));
                        logger.info("EventBus is on the path /eventbus/");
                        startPromise.complete();
                    } else {
                        logger.error("Failed to start http server", r.cause());
                        startPromise.fail(r.cause());
                    }
                    logger.info("VertxHttpServer deployed!");
                });
        router.get("/nio/download").handler(this::download);
        router.get("/nio/download-fix").handler(this::downloadFix);
    }

    /**
     * download but having back pressure issue
     */
    private void download(RoutingContext ctx) {
        logger.info("\nStarting Download the large file using non-blocking I/O with issues.");
        final EventBusNotification notification = new EventBusNotification(vertx, Main.getFileSize());
        vertx.fileSystem().open(Main.DOWNLOAD_FILE_PATH.toAbsolutePath().toString(), new OpenOptions().setRead(true), ar -> {
            if (ar.succeeded()) {
                AsyncFile file = ar.result();
                ctx.response().setChunked(true)
                        .setStatusCode(200)
                        .putHeader("Content-Type", "application/octet-stream")
                        .putHeader("Cache-Control", "no-cache")
                ;
                file.handler(buffer -> {
                    final int len = buffer.length();
                    notification.notice(len, 0);
                    ctx.response().write(buffer, h -> {
                        if (h.succeeded()) {
//                            vertx.setTimer(500, l -> notification.notice(0, len));
//                            logger.info("Notice write len: " + len + " bytes");
                            notification.notice(0, len);
                        } else {
                            h.cause().printStackTrace();
                            ctx.fail(h.cause());
                        }
                    });
                })
                    .exceptionHandler(ctx::fail)
                    .endHandler(h -> {
                        ctx.end();
                        logger.info("Non-Blocking I/O downloaded");
                        logger.info("Total Read(Nio): " + notification.getTotalRead() + " bytes");
                        logger.info("Total Written(Nio): " + notification.getTotalWritten() + " bytes\n");
                })
                ;
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    private void downloadFix(RoutingContext ctx) {
        // download with the back pressure fixed
    }
}
