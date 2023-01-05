# vertx-demos-service-proxy

The demo of showing how vertx-service-proxy is used

## Structure of the demo

The demo runs a `2` nodes cluster using `hazelcast` cluster manager, one node is `service node` which registers the DBService implementation on event bus address: `db.service`, the other node is `client node` which starts a http server waiting for requests for operations to the DBService.

There will be a `DBServiceVertxEBProxy` generated and running on the client node, which will delegate all the DBService method calls via the eventbus, it is included in the produced artifact with `api` classifier:

```xml
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>vertx-demos-service-proxy-service</artifactId>
    <classifier>api</classifier>
    <version>${project.version}</version>
</dependency>
```

The `service` module also generates a `sockjs` service proxy which can be used in a NodeJS application. The generated TypeScript/Javascript are included in the produced artifact with `js` classifier:

```xml
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>vertx-demos-service-proxy-service</artifactId>
    <classifier>js</classifier>
    <version>${project.version}</version>
</dependency>
```

The client node also set up a [SockJS EventBus Bridge](https://vertx.io/docs/vertx-web/java/#_sockjs_event_bus_bridge) in the HTTP Server to bridge eventbus messages between web browser and other places in the eventbus.


## How to run the demo

* Build the project

> mvn clean install

* In the root directory of the project, start the services needed for the demo:
> docker-compose up

* In another terminal, run:
> cd service/ && ./run-service.sh

You will see the following output:
```shell
 ===   DBService registered   ===
```
which indicates that the DBService has been registered in the clustered eventbus.

* In another terminal, run:
> cd client && ./run-client.sh

You will see the following outut:
```shell
Starts HttpServer at: 8000
```
which indicates that the client has joined the cluster and start a http server on port: `8000`

* Again, in another terminal, run:
> http POST :8000/save/lgao/ message=hello

which will try to create a new DataEntry, with name: `lgao`, and message: `hello`, then the client node will call the `dbSerivce.save(entry)` which delegate the real service deployed in service node to store the data into database.

* You can list all the data using:
> http :8000/list

```shell
[🎩 lgao@lins-p1 client]$ http :8000/list
HTTP/1.1 200 OK
Content-Type: application/json
content-length: 459

[
    {
        "id": 1558240367,
        "message": "hello",
        "name": "lgao"
    }
]

```

* There is a one-page application(OPA) NodeJS application running at `http://localhost:8000/`, open it using your web browser, you can see the demonstration on the page.
   * Input the name and message that you want to send, and click the Send button, you will see the message got sent to the service node, and you will see the messagae is listed in the right.