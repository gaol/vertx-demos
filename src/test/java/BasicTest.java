/*
 *  Copyright (c) 2022 The original author or authors
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

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.healthchecks.CheckResult;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.junit5.VertxTestContext;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
@RunWith(VertxUnitRunner.class)
public class BasicTest {

    @Test
    public void test() {

        VertxTestContext testContext = new VertxTestContext();
        Vertx vertx = Vertx.vertx();
        HealthChecks healthChecks = HealthChecks.create(vertx);
        healthChecks.register("test", promise -> promise.complete(Status.OK()));

        vertx.eventBus().consumer("health",
                message -> healthChecks.checkStatus()
                        .onSuccess(r -> message.reply(r.toJson()))
                        .onFailure(err -> message.fail(0, err.getMessage())));

        vertx.eventBus().<CheckResult>request("health", "check").map(Message::body)
                .onComplete(testContext.succeeding(checkResult -> {
                    System.out.println("Data: " + checkResult.getStatus());
                    testContext.completeNow();
                }));
    }
}
