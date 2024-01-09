package io.vertx.examples.webclient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    public void start() {
        logger.info("Test to send multipart/form-data to remote http endpoint");
        final String host = System.getProperty("http.host", "localhost");
        final int port = Integer.getInteger("http.port", 8080);
        final String path = System.getProperty("upload.path", "/servlet-example/upload");
        logger.info("\n====\nTrying to send multipart/form-data request to : http://{}:{}{}\n====\n", host, port, path);
        final String shortParam = System.getProperty("short.param", "text");
        final String shortParamValue = System.getProperty("short.param.value", "example short text");
        final String fileParam = System.getProperty("file.param", "long");
        final String fileName = System.getProperty("file.name", "");
        final String filePath = System.getProperty("file.path");
        if (filePath == null || filePath.equals("")) {
            throw new RuntimeException("Please specify the file path to upload via -Dfile.path=xxx");
        }
        if (!vertx.fileSystem().existsBlocking(filePath)) {
            throw new RuntimeException("File: " + filePath + " does not exist!");
        }
        final String fileContentType = System.getProperty("file.content.type", "application/octet");
        logger.info("\n====\nShort parameter is: {} = {}\n====\n", shortParam, shortParamValue);
        logger.info("\n====\nFile parameter is: {} = {}, filename is: {}, fileContentType is: {}\n====\n", fileParam, filePath, fileName, fileContentType);
        final MultipartForm form = MultipartForm.create()
                .binaryFileUpload(fileParam, fileName, filePath, fileContentType);
        WebClient webClient = WebClient.create(vertx);
        webClient.post(port, host, path)
                .addQueryParam(shortParam, shortParamValue)
                .sendMultipartForm(form).onComplete(r -> {
                    if (r.succeeded()) {
                        System.out.println(r.result().body().toString());
                    } else {
                        r.cause().printStackTrace();
                    }
                    vertx.close();
                });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        // deploy 1 instance
        vertx.deployVerticle(new Main());
    }
}
