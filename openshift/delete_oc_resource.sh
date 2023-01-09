oc delete bc vertx-demo-openshift-http-s2i
oc delete dc vertx-demo-openshift-http
oc delete service/vertx-demo-openshift-http
oc delete imagestream.image.openshift.io/vertx-demo-openshift-http

oc delete bc vertx-demo-openshift-service-s2i
oc delete dc vertx-demo-openshift-service
oc delete service/vertx-demos-openshift-service
oc delete imagestream.image.openshift.io/vertx-demo-openshift-service

oc delete route.route.openshift.io/vertx-demo-openshift-http


oc delete service vertx-demo-openshift-cluster
