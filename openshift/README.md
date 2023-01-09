# vertx-demos

This demo shows how to run a vertx cluster in Openshift environment.

It is an 2 nodes cluster running on Openshift platform, there is one verticle running on each deployment(different pod).

One deployment runs a `HttpVerticle` which starts an HTTP server with 2 endpoints:

* `/`  . The root endpoint answers house price of a specified city.
* `/cities`  . The `/cities` endpoint lists all cities that the demo has data with.

> NOTE: all data are collected and cached on 2022-11-09 from https://fangjia.gotohui.com/fjdata-1

The `HttpVerticle` will convert all http requests to EventBus messages and request the answers from the eventbus addresses: `city-house-price` or `cities`. The consumers registered on the addresses run on a different deployment in Openshift.

The other deployment runs a `ServiceVerticle` which registers a consumer on address: `city-house-price` to answer the house price of the city from the message body. It also registers another consumer on address: `cities` to answer all city information without having requirement on the message body.


## Running in OpenShift

It supposes you have created a project called: `vertx-demo-openshift`, which the namespace is used in the examples.

Steps on OpenShift:

* Create project

> `oc new-project vertx-demo-openshift`

* Create headless Service, this is needed to form the infinispan cluster.

> `oc create -f headless-service.yaml`

* Build the project, run the following command in the `openshift/` directory:

> mvn clean install

* Deploy Http

In `demo-openshift-http` folder:

> `mvn -Popenshift clean oc:resource`
>
> `mvn -Popenshift package oc:build`
>
> `mvn -Popenshift oc:apply`

* Deploy service

Like what it is done above, in `demo-openshift-service` folder:

> `mvn -Popenshift clean oc:resource`
>
> `mvn -Popenshift package oc:build`
>
> `mvn -Popenshift oc:apply`

Checking pods or services:

> `oc get pods`
> 
> `oc get services`


The `maven-openshift-plugin` will create routes for http requests.

> `oc get routes`


## Test 

When both verticles are deployed into Openshift, open a terminal and run:

*  `http vertx-demo-openshift-http-vertx-demo-openshift.apps-crc.testing/?city=bj`

```shell
[🎩 lgao@lins-p1 demo-openshift-service]$ http vertx-demo-openshift-http-vertx-demo-openshift.apps-crc.testing/
HTTP/1.1 200 OK
cache-control: private
content-length: 45
set-cookie: be84f87eb9d33e9dc7a28851b13cb9b5=8bc5c52a2a9145e0a23cef9eaadf7090; path=/; HttpOnly

{
  "name" : "Beijing",
  "price" : 65901.0
}

```
* `http vertx-demo-openshift-http-vertx-demo-openshift.apps-crc.testing/cities` will list all city information this demo knows.

```shell
[🎩 lgao@lins-p1 demo-openshift-service]$ http vertx-demo-openshift-http-vertx-demo-openshift.apps-crc.testing/cities
HTTP/1.1 200 OK
cache-control: private
content-length: 479
set-cookie: be84f87eb9d33e9dc7a28851b13cb9b5=8bc5c52a2a9145e0a23cef9eaadf7090; path=/; HttpOnly

{
  "cities" : 8,
  "avg" : 36912.125,
  "data" : [ {
    "name" : "Chengdu",
    "price" : 17060.0
  }, {
    "name" : "Qingdao",
    "price" : 22419.0
  }, {
    "name" : "Shanghai",
    "price" : 68909.0
  }, {
    "name" : "Tianjin",
    "price" : 23834.0
  }, {
    "name" : "Shenzhen",
    "price" : 65893.0
  }, {
    "name" : "Beijing",
    "price" : 65901.0
  }, {
    "name" : "Xian",
    "price" : 17294.0
  }, {
    "name" : "Chongqing",
    "price" : 13987.0
  } ]
}

```

## Undeploy the deployments

Use the following command in each folder to un deploy the services:

> `mvn -Popenshift oc:undeploy`

In case any error, try to use `./delete_oc_resource.sh` to release resource manually.

## Delete the openshift project

Run the following command

> oc delete project vertx-demo-openshift

to delete the project from the Openshift cluster.
