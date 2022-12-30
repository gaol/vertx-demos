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
package io.vertx.examples.serviceproxy;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.util.List;

/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
@ProxyGen // used to generate service proxy stub
@VertxGen // used for polyglot support, like
public interface DBService {

    static DBService createService(Vertx vertx) {
        return new DBServiceImpl(vertx);
    }

    static DBService serviceProxy(Vertx vertx, String address) {
        return new DBServiceVertxEBProxy(vertx, address);
    }

    /**
     * Save data into database.
     *
     * @param data the data to save
     * @return a Future
     */
    Future<Void> save(DataEntry data);

    /**
     * Loads data from database.
     *
     * @return a Future
     */
    Future<List<DataEntry>> load();

    /**
     * Close to release the resources.
     *
     * @return a Future
     */
    @GenIgnore
    default Future<Void> close() {
        return Future.failedFuture("not supported");
    }

}
