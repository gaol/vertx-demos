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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static io.vertx.examples.backpressure.Main.CHUNK_SIZE;

/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
public class DownloadUndertowHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger("Undertow-Download-Handler");
    private final Vertx vertx;
    private final long fileSize;
    private final Path downloadFile;
    DownloadUndertowHandler(Vertx vertx, Path downloadFile, long fileSize) {
        this.vertx = vertx;
        this.fileSize = fileSize;
        this.downloadFile = downloadFile;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        logger.info("Starting Download the large file using blocking I/O.");
        final EventBusNotification notification = new EventBusNotification(vertx, fileSize);
        exchange.getResponseHeaders()
                .put(Headers.CONTENT_TYPE, "application/octet-stream")
                .put(Headers.CACHE_CONTROL, "no-cache");
        try (InputStream input = Files.newInputStream(downloadFile, StandardOpenOption.READ);
             OutputStream output = exchange.getOutputStream()) {
            // small buffer to simulate the stream clearly
            byte[] buffer = new byte[CHUNK_SIZE];
            int len;
            while ((len = input.read(buffer)) != -1) {
                notification.notice(len, 0);
                // it blocks when data has been not flushed to remote
                output.write(buffer, 0, len);
                output.flush();
                notification.notice(0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        logger.info("Blocking I/O downloaded");
        logger.info("Total Read: " + notification.getTotalRead() + " bytes");
        logger.info("Total Written: " + notification.getTotalWritten() + " bytes\n");
    }

}
