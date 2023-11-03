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

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.atomic.AtomicLong;

import static io.vertx.examples.backpressure.Main.MESSAGE_ADDR;

/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
public class EventBusNotification {
    private final Vertx vertx;
    private final long fileSize;
    private final AtomicLong totalRead = new AtomicLong(0);
    private final AtomicLong totalWritten = new AtomicLong(0);
    private final AtomicLong buffersInMemory = new AtomicLong(0);
    private final Context ctx;

    public EventBusNotification(Vertx vertx, long fileSize) {
        this.vertx = vertx;
        this.fileSize = fileSize;
        ctx = vertx.getOrCreateContext();
    }

    public void notice(int readLen, int writeLen) {
        ctx.runOnContext(v -> vertx.eventBus().publish(MESSAGE_ADDR, new JsonObject()
                .put("write", totalWritten.addAndGet(writeLen))
                .put("read", totalRead.addAndGet(readLen))
                .put("buffers", buffersInMemory.addAndGet((readLen - writeLen)))
                .put("fileSize", fileSize)));
    }

    public long getTotalRead() {
        return totalRead.longValue();
    }

    public long getTotalWritten() {
        return totalWritten.longValue();
    }
}
