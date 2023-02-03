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
package io.vertx.examples.hibernate.reactive;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.ext.web.handler.BodyHandler;
import org.hibernate.reactive.mutiny.Mutiny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Persistence;
import java.util.List;

/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
public class Main extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private Mutiny.SessionFactory emf;
    @Override
    public Uni<Void> asyncStart() {
        logger.info("start deploying...");
        Uni<Void> startHibernate = vertx.executeBlocking(Uni.createFrom().deferred(() -> {
            emf = Persistence.createEntityManagerFactory("pg-demo")
                    .unwrap(Mutiny.SessionFactory.class);
            return Uni.createFrom().voidItem();
        }));
        Router router = Router.router(vertx);
        BodyHandler bodyHandler = BodyHandler.create();
        router.post().handler(bodyHandler::handle);
        router.get("/list").respond(rc -> listDataEntry());
        router.post("/save").respond(this::saveDataEntry);
        Uni<Void> startHttpServer = vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080)
                .onItem().invoke(() -> logger.info("HttpServer started on port: 8080"))
                .replaceWithVoid();
        return Uni.combine().all().unis(startHibernate, startHttpServer).discardItems();
    }

    private Uni<Object> saveDataEntry(RoutingContext ctx) {
        logger.info("save dataentry");
        JsonObject json = ctx.body().asJsonObject();
        String name = json.getString("name", "<default-name>");
        String message = json.getString("message", "<default-message>");
        DataEntry entry = new DataEntry().setName(name).setMessage(message);
        return emf.withSession(session -> session.persist(entry)
                .chain(session::flush).replaceWith(entry));
    }

    private Uni<List<DataEntry>> listDataEntry() {
        logger.info("list data entries");
        return emf.withSession(session ->
                session.createQuery("FROM DataEntry", DataEntry.class)
                        .getResultList());
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new Main())
                .subscribe()
                .with(good -> logger.info("all good"), err -> logger.error("something wrong", err));
    }
}
