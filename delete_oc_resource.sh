oc delete deploymentconfig.apps.openshift.io/vertx-demos-http
oc delete buildconfig.build.openshift.io/vertx-demos-service-s2i
oc delete replicationcontroller/vertx-demos-http-1
oc delete deploymentconfig.apps.openshift.io/vertx-demos-service
oc delete buildconfig.build.openshift.io/vertx-demos-http-s2i
oc delete build.build.openshift.io/vertx-demos-http-s2i-1
oc delete service/vertx-demo-cluster
oc delete service/vertx-demos-http
oc delete service/vertx-demos-service
oc delete imagestream.image.openshift.io/vertx-demos-http
oc delete imagestream.image.openshift.io/vertx-demos-service
oc delete route.route.openshift.io/vertx-demos-http
