# vertx-demos-backpressure

Vertx demos to show how backpressure is addressed

The project simply shows how downloading a big file from server consumes memory in the server side.

It assumes there is a file located at `~/large.bin` by default, you can also specify via system property `download.file.path` to use different file for the demonstration.

## Build the project

> mvn clean install

## Run the server

> java -jar target/vertx-demos-backpressure-0.0.1-fat.jar

or specify different file for the demonstration

> java -Ddownload.file.path=xxx.file -jar target/vertx-demos-backpressure-0.0.1-fat.jar

You will see the logs like:

```bash
[🎩 lgao@lins-p1 backpressure]$ java -jar target/vertx-demos-backpressure-0.0.1-fat.jar 
[main] INFO main - File Size to be downloaded has 575274048 bytes
[main] INFO main - Starting the Undertow server at port 8080 ...
Nov 05, 2023 8:20:47 PM io.undertow.Undertow start
INFO: starting server: Undertow - 2.3.5.Final
Nov 05, 2023 8:20:47 PM org.xnio.Xnio <clinit>
INFO: XNIO version 3.8.8.Final
Nov 05, 2023 8:20:47 PM org.xnio.nio.NioXnio <clinit>
INFO: XNIO NIO Implementation Version 3.8.8.Final
Nov 05, 2023 8:20:47 PM org.jboss.threads.Version <clinit>
INFO: JBoss Threads version 3.5.0.Final
[main] INFO main - Undertow server started!
[main] INFO main - 	 ======
[main] INFO main - Starting Vertx instance for Non Blocking Downloader...
[vert.x-eventloop-thread-0] INFO VertxHttpServer - Starts HttpServer using Vert.x at: 8000
[vert.x-eventloop-thread-0] INFO VertxHttpServer - EventBus is on the path /eventbus/
[vert.x-eventloop-thread-0] INFO VertxHttpServer - VertxHttpServer deployed!
[vert.x-eventloop-thread-1] INFO main - Download the file using blocking i/o at: http://localhost:8080/bio/download
[vert.x-eventloop-thread-1] INFO main - Download the file using non blocking i/o at: http://localhost:8000/nio/download
[vert.x-eventloop-thread-1] INFO main - Download the file using non blocking i/o with fix at: http://localhost:8000/nio/download-fix
```

The server starts 2 http servers, one is using undertow to start a server at port `8080` for the blocking i/o downloading, the other is using vert.x to start a http server at port `8000` for the non-blocking i/o downloading, it also sets up the sockjs eventbus bridge so that the statistics data can be displayed in a web page.

## See the UI for the demonstration

Open your web browser to access http://localhost:8000/

You will see some diagrams and some information like:
* Total buffers read `xxx` bytes
* Total buffers written `xxx` bytes
* Buffers in server currently `xxx` bytes

## Testing using blocking i/o

> curl -s "http://localhost:8080/bio/download" --output out.bio

This will download the file from the undertow http server using blocking i/o, at the same time, the web ui will show the buffers in the server in realtime.

### emulates slow network

Switch to `root` via `su -` and run:
> tc qdisc del dev lo root netem delay 50ms

Now the network traffic using `lo` net device has `50ms` of delay. You will see that there is no difference of memory usage.

Remember to remove the network delay via:
> tc qdisc del dev lo root netem delay 50ms

## Testing using non-blocking i/o having problems

> curl -s "http://localhost:8000/nio/download" --output out.nio

This will download the file from the vertx http server using non-blocking i/o, at the same time, the web ui will show the buffers in the server in realtime.

We need to enable network delay to show the problem more clearly.

## Testing using non-blocking i/o without problems

> curl -s "http://localhost:8000/nio/download-fix" --output out.nio.fix



