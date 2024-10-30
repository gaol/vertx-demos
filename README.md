# vertx-demos

This branch `mail` is used to run local tests with thousands of mails using `ab` requests to a HTTP endpoint.

The `total` parameter is used as the expected total mails to send, after a while, there will be statistics in the server logs.

## Start mail server

```shell
git clone https://github.com/gaol/docker-images

cd docker-images/mails/mail_james
podman build .
./start.sh
# after a while in another terminal
./init_data.sh
```

Run a local email using Apache James with the doc

## Build and Start demo

> mvn clean install
> java -jar target/vertx-demos-mail-0.0.1-fat.jar

## send 1000 emails with 100 concurrent requests
> ab -n 1000 -c 100 http://localhost:8888/sendmail?total=1000

## Check logs

```asciidoc
		Total Statistics
Plan  sent  emails: 	1000
Actual sent emails: 	1000

Sending in each event loop:
Thread: vert.x-eventloop-thread-7  :  	sent 125 emails.
Thread: vert.x-eventloop-thread-3  :  	sent 125 emails.
Thread: vert.x-eventloop-thread-5  :  	sent 125 emails.
Thread: vert.x-eventloop-thread-2  :  	sent 125 emails.
Thread: vert.x-eventloop-thread-1  :  	sent 125 emails.
Thread: vert.x-eventloop-thread-4  :  	sent 125 emails.
Thread: vert.x-eventloop-thread-8  :  	sent 125 emails.
Thread: vert.x-eventloop-thread-6  :  	sent 125 emails.
Sent emails count(calculated): 	1000

 Time Consumed 
Start time: 	2024 10 30 14:16:17
End time: 	2024 10 30 14:17:46
Time elapsed: 	88 s

```
