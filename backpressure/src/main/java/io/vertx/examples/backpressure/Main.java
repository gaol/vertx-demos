package io.vertx.examples.backpressure;

import io.undertow.Undertow;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.PathHandler;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger("main");

    // large.bin is a big file with 549MB
    static final Path DOWNLOAD_FILE_PATH = Path.of(System.getProperty("download.file.path", System.getProperty("user.home") + "/large.bin"));
    static final String MESSAGE_ADDR = "buffer.update";
    static final int CHUNK_SIZE = 1024;

    private static Long fileSize;

    public static void main(String[] args) {
        logger.info("File Size to be downloaded has " + getFileSize() + " bytes");
        logger.info("Starting the Undertow server at port 8080 ...");
        final Vertx vertx = Vertx.vertx();
        vertx.exceptionHandler(t -> logger.error("Failed out in vertx global exception handler", t));
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(new PathHandler().addPrefixPath("/bio/download", new BlockingHandler(new DownloadUndertowHandler(vertx))))
                .build();
        server.start();
        logger.info("Undertow server started!");
        logger.info("\t ======");
        logger.info("Starting Vertx instance for Non Blocking Downloader...");
        vertx.deployVerticle(new VertxHttpServer());
        vertx.setTimer(1000, l -> {
            logger.info("Download the file using blocking i/o at: http://localhost:8080/bio/download");
            logger.info("Download the file using non blocking i/o at: http://localhost:8000/nio/download");
            logger.info("Download the file using non blocking i/o with fix at: http://localhost:8000/nio/download-fix");
        });
    }

    static synchronized long getFileSize() {
        if (fileSize == null) {
            try {
                fileSize = Files.size(Main.DOWNLOAD_FILE_PATH);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return fileSize;
    }

}
