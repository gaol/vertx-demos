# vertx-demos-polyglot

Vertx demos to deploy verticles in different languages

This demo contains `4` verticles written in:
* Java
* Kotlin
* Groovy
* Javascript

All of them start a http server with different ports.

* Build the demo:

> mvn clean install

* Start the app
> ./run.sh

Then you will see the following output:

```shell
[🎩 lgao@lins-p1 polyglot]$ ./run.sh 
Starts HttpServer from Groovy at: 8001
Starts HttpServer from Kotlin at: 8002
Starts HttpServer from Java at: 8000
Succeeded in deploying verticle
Starts HttpServer from JavaScript at: 8003

```

* Open another terminal, and try with the following commands:

   * Access `8080` from Java verticle:
    ```shell
    [🎩 lgao@lins-p1 polyglot]$ http :8000
    HTTP/1.1 200 OK
    Content-Type: text/html
    content-length: 61
    
    <html><body><h1>Hello from vert.x in Java!</h1></body></html>
    ```

    * Access `8081` from Groovy verticle:
    ```shell
    [🎩 lgao@lins-p1 polyglot]$ http :8001
    HTTP/1.1 200 OK
    Content-Type: text/html
    content-length: 63
    
    <html><body><h1>Hello from vert.x in Groovy!</h1></body></html>
    ```

    * Access `8082` from Kotlin verticle:
    ```shell
    [🎩 lgao@lins-p1 polyglot]$ http :8002
    HTTP/1.1 200 OK
    Content-Type: text/html
    content-length: 63
    
    <html><body><h1>Hello from vert.x in Kotlin!</h1></body></html>
    ```

    * Access `8083` from JavaScript verticle:
    ```shell
  [🎩 lgao@lins-p1 polyglot]$ http :8003
  HTTP/1.1 200 OK
  Content-Type: text/html
  content-length: 67
  
  <html><body><h1>Hello from vert.x in JavaScript!</h1></body></html>
    ```
