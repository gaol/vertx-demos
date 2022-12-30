# vertx-demos-service-proxy

The demo of showing how vertx-service-proxy is used

## Structure of the demo

The demo runs a `2` nodes cluster, one node is `service node` which registers the DBService implementation on event bus: `db.service`, the other node is `client node` which starts a http server waiting for requests for operations to the DBService. There will be a `DBServiceVertxEBProxy` generated and running on the client node, which will delegate all the DBService methods call via the eventbus.

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

