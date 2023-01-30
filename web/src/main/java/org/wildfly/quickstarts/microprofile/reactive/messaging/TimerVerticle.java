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
package org.wildfly.quickstarts.microprofile.reactive.messaging;

import io.vertx.core.AbstractVerticle;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
public class TimerVerticle extends AbstractVerticle {
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
    public final static String TIME_REPORT_ADDRESS = "report-time";

    @Override
    public void start() throws Exception {
        vertx.setPeriodic(1000, l -> {
            Instant now = Instant.now();
            String zoneId = "Asia/Shanghai";
            ZonedDateTime chinaTime = now.atZone(ZoneId.of(zoneId));
            vertx.eventBus().publish(TIME_REPORT_ADDRESS, "Time at zone: " + zoneId + " is " + dtf.format(chinaTime));
        });
    }
}
