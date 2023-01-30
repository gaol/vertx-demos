# vertx-demos

This is a demo to show how the [microprofile-reactive-messaging-kafka](https://github.com/wildfly/quickstart/tree/main/microprofile-reactive-messaging-kafka) quickstart can be extended to run with  [wildfly-vertx-extension](https://github.com/gaol/wildfly-vertx-extension/).

Based on the quickstart, this changed the DatabaseBean to use a `vertx-pg-client` to store the data into and retrieve the data from a PostgreSQL server running in a container.

Steps to run the demo:

* Start the services using docker-compose: `docker-compose up`

* Build the project using `mvn clean install`

* Start the server with `*-full-ha.xml`, it can be used for cluster mode demo.
  > cd ~/sources/wildfly/wildfly-vertx-extention
  > 
  > dist/target/wildfly-vertx-dist-1.0.0-SNAPSHOT/bin/standalone.sh -c standalone-vertx-full-ha.xml

* Enable `microprofile-reactive-streams-operators-smallrye` subsystem and `microprofile-reactive-messaging-smallrye` by using the `enable-reactive-messaging.cli` file in this directory.
   > change directory to source of vertx-demos
   > 
   > ~/sources/wildfly/wildfly-vertx-extention/dist/target/wildfly-vertx-dist-1.0.0-SNAPSHOT/bin/jboss-cli.sh -c --file=enable-reactive-messaging.cli

* Deploy the war file to server:
  > change directory to source of vertx-demos
  >
  > ~/sources/wildfly/wildfly-vertx-extention/dist/target/wildfly-vertx-dist-1.0.0-SNAPSHOT/bin/jboss-cli.sh -c --file=deploy-demo.cli

* Open a terminal for SSE stream from server:
  > http :8080/vertx-demos-kafka-web-0.0.1/user

* Open another terminal to send message to server for processing
  > http POST :8080/vertx-demos-kafka-web-0.0.1/user/&lt;message&gt;
   * Use the following `message` for demos:
     * `only-sse1`   # this message passes in-vm stream, kafka topic, then store to db.
     * `stop-on-vm`  # this message passes in-vm stream only, not in kafka topic, no db store.
     * `Vertx`       # this message passes in-vm, kafka, then store to db, and forward to vertx eventbus with address: `to-address`

* Open another terminal to start the verticle to join the cluster:
   > ./start-verticle.sh

* Open another terminal for SSE from verticle:
   > http :9080

* When done, undeploy it using:
   > change directory to source of vertx-demos
   >
   > ~/sources/wildfly/wildfly-vertx-extention/dist/target/wildfly-vertx-dist-1.0.0-SNAPSHOT/bin/jboss-cli.sh -c --file=undeploy-demo.cli
