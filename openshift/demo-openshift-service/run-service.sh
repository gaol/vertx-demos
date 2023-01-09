#!/bin/bash

java -Djava.net.preferIPv4Stack=true -Dvertx.jgroups.config=jgroups-tcp.xml -jar target/vertx-demo-openshift-service-*-fat.jar -cluster --cluster-host=127.0.0.1


