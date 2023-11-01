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

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import static io.vertx.examples.backpressure.Main.CHUNK_SIZE;
import static io.vertx.examples.backpressure.Main.MESSAGE_ADDR;

/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
public class DownloadUndertowHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger("Undertow-Download-Handler");

    private final long fileSize;

    {
        try {
            fileSize = Files.size(Main.DOWNLOAD_FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Vertx vertx;
    DownloadUndertowHandler(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        logger.info("Starting Download the large file using blocking I/O.");
        exchange.getResponseHeaders()
                .put(Headers.CONTENT_TYPE, "application/octet-stream")
                .put(Headers.CACHE_CONTROL, "no-cache");
        try (InputStream input = fileInputStream();
             OutputStream output = httpOutStream(exchange)) {
            // small buffer to simulate the stream clearly
            byte[] buffer = new byte[CHUNK_SIZE];
            int len;
            while ((len = input.read(buffer)) > 0) {
                // it blocks when data has been not flushed to remote
                output.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        logger.info("Blocking I/O downloaded");
    }

    private OutputStream httpOutStream(HttpServerExchange exchange) {
        return new FilterOutputStream(exchange.getOutputStream()) {
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                super.write(b, off, len);
                vertx.eventBus().publish(MESSAGE_ADDR, new JsonObject().put("update", len).put("fileSize", fileSize));
            }
        };
    }

    private InputStream fileInputStream() throws IOException {
        logger.info("Trying to download File: " + Main.DOWNLOAD_FILE_PATH);
        return new FilterInputStream(Files.newInputStream(Main.DOWNLOAD_FILE_PATH, StandardOpenOption.READ)) {
            @Override
            public int read() throws IOException {
                int r = super.read();
                if (r > 0) {
                    vertx.eventBus().publish(MESSAGE_ADDR, new JsonObject().put("update", -r).put("fileSize", fileSize));
                }
                return r;
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                int r = super.read(b, off, len);
                if (r > 0) {
                    vertx.eventBus().publish(MESSAGE_ADDR, new JsonObject().put("update", -r).put("fileSize", fileSize));
                }
                return r;
            }
        };
    }

}
