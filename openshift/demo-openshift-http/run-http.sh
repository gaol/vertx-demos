#!/bin/bash

java -Djava.net.preferIPv4Stack=true -Dvertx.jgroups.config=jgroups-tcp.xml -jar target/vertx-demo-openshift-http-*-fat.jar -cluster --cluster-host=127.0.0.1


