/*
 * JBoss, Home of Professional Open Source
 * Copyright 2021, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.quickstarts.microprofile.reactive.messaging;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

/**
 * @author <a href="mailto:kabir.khan@jboss.com">Kabir Khan</a>
 */

@ApplicationScoped
public class DatabaseBean {

    @Inject
    private Vertx vertx;

    @Inject
    @ConfigProperty(name = "db.postgresql.connection.url")
    private String dbConnectionUrl;

    private PgPool pool;

    @PostConstruct
    public void postConstruct() {
        PgConnectOptions connectOptions = PgConnectOptions.fromUri(dbConnectionUrl);
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
        pool = PgPool.pool(vertx, connectOptions, poolOptions);
    }

    @PreDestroy
    public void destroy() {
        try {
            pool.close().toCompletionStage().toCompletableFuture().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CompletionStage<Void> store(TimedEntry entry) {
        return pool.getConnection()
                .compose(conn -> conn.preparedQuery("INSERT INTO TimedEntry (id, time, message) VALUES ($1, $2, $3)")
                        .execute(Tuple.of(entryId(entry), dateTime(entry.getTime().getTime()), entry.getMessage()))
                        .eventually(v -> conn.close()))
                .<Void>flatMap(rows -> Future.succeededFuture())
                .toCompletionStage();
    }

    public Future<List<TimedEntry>> loadAllTimedEntries() {
        return pool.getConnection().compose(conn -> conn.preparedQuery("SELECT id, time, message FROM TimedEntry")
                        .mapping(timedEntryMapper)
                        .execute()
                        .eventually(v-> conn.close()))
                .map(timedEntries -> {
                    List<TimedEntry> result = new ArrayList<>();
                    timedEntries.forEach(result::add);
                    return result;
                })
        ;
    }

    private static final Function<Row, TimedEntry> timedEntryMapper = row -> {
        Long id = row.getLong("id");
        Timestamp time = Timestamp.valueOf(row.getLocalDateTime("time"));
        String message = row.getString("message");
        TimedEntry te = new TimedEntry(time, message);
        te.setId(id);
        return te;
    };

    private LocalDateTime dateTime(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }

    private long entryId(TimedEntry entry){
        return entry.getId() == null ? System.nanoTime() : entry.getId();
    }
}