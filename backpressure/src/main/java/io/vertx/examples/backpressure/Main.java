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
    static final String MESSAGE_ADDR = "buffer.update";
    static final int CHUNK_SIZE = 8192;

    private final Long fileSize;
    private final Vertx vertx;

    private final Path downloadFile;

    public Main() {
        this.vertx = Vertx.vertx();
        this.downloadFile = Path.of(System.getProperty("download.file.path", System.getProperty("user.home") + "/large.bin"));
        try {
            this.fileSize = Files.size(this.downloadFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.execute();
    }

    private void execute() {
        logger.info("File Size to be downloaded has " + getFileSize() + " bytes");
        logger.info("Starting the Undertow server at port 8080 ...");
        vertx.exceptionHandler(t -> logger.error("Failed out in vertx global exception handler", t));
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(new PathHandler().addPrefixPath("/bio/download", new BlockingHandler(new DownloadUndertowHandler(vertx, getDownloadFile(), getFileSize()))))
                .build();
        server.start();
        logger.info("Undertow server started!");
        logger.info("\t ======");
        logger.info("Starting Vertx instance for Non Blocking Downloader...");
        vertx.deployVerticle(new VertxHttpServer(io.vertx.rxjava3.core.Vertx.newInstance(vertx), getDownloadFile(), getFileSize())).onSuccess(s -> {
            logger.info("Download the file using blocking i/o at: http://localhost:8080/bio/download");
            logger.info("Download the file using non blocking i/o at: http://localhost:8000/nio/download");
            logger.info("Download the file using non blocking i/o with fix at: http://localhost:8000/nio/download-fix");
        }).onFailure(Throwable::printStackTrace);
    }

    public long getFileSize() {
        return fileSize;
    }

    public Vertx getVertx() {
        return vertx;
    }

    public Path getDownloadFile() {
        return downloadFile;
    }
}
