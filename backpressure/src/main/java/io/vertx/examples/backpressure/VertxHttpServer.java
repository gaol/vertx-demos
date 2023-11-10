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
import io.vertx.core.Handler;
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
import io.vertx.rxjava3.core.buffer.Buffer;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import static io.vertx.examples.backpressure.Main.CHUNK_SIZE;
import static io.vertx.examples.backpressure.Main.MESSAGE_ADDR;

/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
public class VertxHttpServer extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger("VertxHttpServer");

    private final io.vertx.rxjava3.core.Vertx rxVertx;
    private final long fileSize;
    private final Path downloadFile;
    public VertxHttpServer(io.vertx.rxjava3.core.Vertx rxVertx, Path downloadFile, long fileSize) {
        this.rxVertx = rxVertx;
        this.fileSize = fileSize;
        this.downloadFile = downloadFile;
    }

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
        router.get("/nio/download-fix2").handler(this::downloadFix2);
        io.vertx.rxjava3.ext.web.Router rxRouter = io.vertx.rxjava3.ext.web.Router.newInstance(router);
        rxRouter.get("/nio/download-rx").handler(this::downloadFixRx);
    }

    private void setRespHeaders(RoutingContext ctx) {
        ctx.response().setChunked(true)
                .setWriteQueueMaxSize(CHUNK_SIZE)
                .setStatusCode(200)
                .putHeader("Content-Type", "application/octet-stream")
                .putHeader("Cache-Control", "no-cache");
    }

    private void endNio(RoutingContext ctx, EventBusNotification notification) {
        ctx.end();
        logger.info("Non-Blocking I/O downloaded");
        logger.info("Total Read(Nio): " + notification.getTotalRead() + " bytes");
        logger.info("Total Written(Nio): " + notification.getTotalWritten() + " bytes\n");
    }

    /**
     * download but having back pressure issue
     */
    private void download(RoutingContext ctx) {
        logger.info("\nStarting Download the large file using non-blocking I/O with issues.");
        setRespHeaders(ctx);
        final EventBusNotification notification = new EventBusNotification(vertx, this.fileSize);
        vertx.fileSystem().open(downloadFile.toAbsolutePath().toString(), new OpenOptions(), ar -> {
            if (ar.succeeded()) {
                AsyncFile file = ar.result().setReadBufferSize(CHUNK_SIZE);
                file.handler(buffer -> {
                    notification.notice(buffer.length(), 0);
                    ctx.response().write(buffer, h -> {
                        if (h.succeeded()) {
                            notification.notice(0, buffer.length());
                        } else {
                            ctx.fail(h.cause());
                        }
                    });
                })
                .exceptionHandler(ctx::fail)
                .endHandler(h -> endNio(ctx, notification));
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    /**
     * download with the back pressure issue fixed by checking if write queue is full
     */
    private void downloadFix(RoutingContext ctx) {
        logger.info("\nStarting Download the large file using non-blocking I/O with the fix.");
        setRespHeaders(ctx);
        final EventBusNotification notification = new EventBusNotification(vertx, this.fileSize);
        vertx.fileSystem().open(downloadFile.toAbsolutePath().toString(), new OpenOptions(), ar -> {
            if (ar.succeeded()) {
                AsyncFile file = ar.result().setReadBufferSize(CHUNK_SIZE);
                Handler<Void> drainHandler = v -> file.resume();
                file.handler(buffer -> {
                            notification.notice(buffer.length(), 0);
                            ctx.response().write(buffer, h -> {
                                if (h.succeeded()) {
                                    notification.notice(0, buffer.length());
                                } else {
                                    ctx.fail(h.cause());
                                }
                            });
                            if (ctx.response().writeQueueFull()) {
                                file.pause();
                                ctx.response().drainHandler(drainHandler);
                            }
                        })
                        .exceptionHandler(ctx::fail)
                        .endHandler(h -> endNio(ctx, notification));
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    /**
     * download with the back pressure issue fixed by using Pipe
     */
    private void downloadFix2(RoutingContext ctx) {
        logger.info("\nStarting Download the large file using non-blocking I/O with the fix.");
        setRespHeaders(ctx);
        final EventBusNotification notification = new EventBusNotification(vertx, this.fileSize);
        vertx.fileSystem().open(downloadFile.toAbsolutePath().toString(), new OpenOptions(), ar -> {
            if (ar.succeeded()) {
                ar.result()
                    .setReadBufferSize(CHUNK_SIZE)
                    .exceptionHandler(ctx::fail)
                    .endHandler(h -> endNio(ctx, notification))
                    .pipeTo(ctx.response());
            } else {
                ctx.fail(ar.cause());
            }
        });
    }

    /**
     * download with the back pressure issue fixed by using rxjava3 based API
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void downloadFixRx(io.vertx.rxjava3.ext.web.RoutingContext ctx) {
        logger.info("\nStarting Download the large file using non-blocking I/O with the fix using rxjava3.");
        setRespHeaders(ctx.getDelegate());
        final Subscriber<Buffer> subscriber = ctx.response().setWriteQueueMaxSize(CHUNK_SIZE).toSubscriber()
                .onWriteStreamError(ctx::fail)
                .onWriteStreamEnd(ctx::end);
        rxVertx.fileSystem()
                .rxOpen(downloadFile.toAbsolutePath().toString(), new OpenOptions())
                .flatMapPublisher(af -> af.setReadBufferSize(CHUNK_SIZE).toFlowable())
                .subscribe(subscriber::onNext, subscriber::onError, subscriber::onComplete);
    }

}
