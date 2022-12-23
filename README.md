# vertx-demos

This is a demo to show how the microprofile-reactive-messaging-kafka quickstart can be adapted to run in the wildfly-vertx-extension.

Based on the quickstart, this changed the DatabaseBean to use a vertx-pg-client to store the data into and retrieve the data from a PostgreSQL server running in a container.

Steps to run the demo:

* Build the project using `mvn clean install`
* Start the server with `*-full-ha.xml`, it can be used for cluster mode demo.
* Enable `microprofile-reactive-streams-operators-smallrye` subsystem and `microprofile-reactive-messaging-smallrye` by using the `enable-reactive-messaging.cli` file in this directory.
   > Server_HOME/bin/jboss-cli.sh -c --file=enable-reactive-messaging.cli
* Deploy the war file to server:
  > Server_HOME/bin/jboss-cli.sh -c --file=deploy-demo.cli
* Open a terminal for SSE stream from server:
  > http :8080/vertx-demos-kafka-0.0.1/user
* Open another terminal to send message to server for processing
  > http POST :8080/vertx-demos-kafka-0.0.1/user/&lt;message&gt;


* docker pull confluentinc/cp-kafka:6.1.9
* docker pull postgres:10.10
* 