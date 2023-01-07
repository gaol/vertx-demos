#!/bin/bash

java -Dvertx.jgroups.config=jgroups-tcp.xml -jar target/vertx-demos-service-*-fat.jar -cluster --cluster-host=127.0.0.1


