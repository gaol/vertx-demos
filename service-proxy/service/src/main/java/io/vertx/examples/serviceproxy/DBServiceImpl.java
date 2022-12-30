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

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
public class DBServiceImpl implements DBService {
    private final PgPool pool;
    DBServiceImpl(Vertx vertx) {
        super();
        PgConnectOptions connectOptions = new PgConnectOptions()
                .setPort(5432)
                .setHost("localhost")
                .setDatabase("postgres")
                .setUser("postgres")
                .setPassword("postgres");
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
        pool = PgPool.pool(vertx, connectOptions, poolOptions);
    }

    @Override
    public Future<Void> save(DataEntry data) {
        System.out.println("saving data: " + data);
        return pool.getConnection()
                .compose(conn -> conn.preparedQuery("INSERT INTO DataEntry (id, name, message) VALUES ($1, $2, $3)")
                        .execute(Tuple.of(data.getId(), data.getName(), data.getMessage()))
                        .eventually(v -> conn.close()))
                .flatMap(rows -> Future.succeededFuture());
    }

    @Override
    public Future<List<DataEntry>> load() {
        System.out.println("loading all data...");
        return pool.getConnection().compose(conn -> conn.preparedQuery("SELECT id, name, message FROM DataEntry")
                        .mapping(dataEntryMapping)
                        .execute()
                        .eventually(v-> conn.close()))
                .map(timedEntries -> {
                    List<DataEntry> result = new ArrayList<>();
                    timedEntries.forEach(result::add);
                    return result;
                });
    }

    private static final Function<Row, DataEntry> dataEntryMapping = row -> {
        Long id = row.getLong("id");
        String name = row.getString("name");
        String message = row.getString("message");
        DataEntry te = new DataEntry();
        te.setId(id);
        te.setName(name);
        te.setMessage(message);
        return te;
    };

    @Override
    public Future<Void> close() {
        return pool.close()
                .onComplete(v -> System.out.println("!!!  DBService CLOSED  !!!!!"));
    }

}
