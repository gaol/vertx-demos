package io.vertx.examples.backpressure;

import io.undertow.Undertow;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.PathHandler;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger("main");

    // large.bin is a big file with 549MB
    static final Path DOWNLOAD_FILE_PATH = Path.of(System.getProperty("download.file.path", System.getProperty("user.home") + "/large.bin"));
    static final String MESSAGE_ADDR = "buffer.update";
    static final int CHUNK_SIZE = 2048;

    public static void main(String[] args) {
        logger.info("Starting the Undertow server at port 8080 ...");
        final Vertx vertx = Vertx.vertx();
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(new PathHandler().addPrefixPath("/download", new BlockingHandler(new DownloadUndertowHandler(vertx))))
                .build();
        server.start();
        logger.info("Undertow server started!");
        logger.info("\t ======");
        logger.info("Starting Vertx instance for Non Blocking Downloader...");
        vertx.deployVerticle(new VertxHttpServer());
        vertx.setTimer(1000, l -> {
            logger.info("Download the file using blocking i/o at: http://localhost:8080/download");
            logger.info("Download the file using non blocking i/o at: http://localhost:8000/download");
            logger.info("Download the file using non blocking i/o with fix at: http://localhost:8000/download-fix");
        });
    }
}
