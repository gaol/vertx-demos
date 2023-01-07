# vertx-demos
Some Vertx demos


## Running in OpenShift

It supposes you have created a project called: `vertx-demo`, which the namespce is used in the examples.


Steps on OpenShift:

* Create project

> `oc new-project vertx-demo`

* Create headless Service

> `oc create -f headless-service.yaml`

* Deploy Http

In `demos-http` folder:

> `mvn -Popenshift clean oc:resource`
>
> `mvn -Popenshift package oc:build`
>
> `mvn -Popenshift oc:apply`

* Deploy service

In `demos-service` folder:

> `mvn -Popenshift clean oc:resource`
>
> `mvn -Popenshift package oc:build`
>
> `mvn -Popenshift oc:apply`


Use the following command in each folder to un deploy the services:

> `mvn -Popenshift oc:undeploy`

Checking pods or services:

> `oc get pods`
> 
> `oc get services`