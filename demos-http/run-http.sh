#!/bin/bash

java -Dvertx.jgroups.config=jgroups-tcp.xml -jar target/vertx-demos-http-*-fat.jar -cluster --cluster-host=127.0.0.1


