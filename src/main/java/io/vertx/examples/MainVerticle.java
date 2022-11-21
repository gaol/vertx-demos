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
package io.vertx.examples;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
public class MainVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) {
        vertx.deployVerticle(new HttpVerticle())
                .flatMap(d -> vertx.deployVerticle(new ServiceVerticle()))
                .<Void>mapEmpty()
                .onComplete(startPromise);
    }
}
