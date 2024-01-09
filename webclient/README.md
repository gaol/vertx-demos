# vertx-demos-webclient

Vertx demo to show webclient usage

## Build

> mvn clean install

## Run

The built application can be used to send multipart/formdata http request to a remote http endpoint, like:

> java -jar target/vertx-demos-weblcient-0.0.1-fat.jar -Dfile.path=xxx

or

> mvn exec:java -Dfile.path=xxx

More parameters are:

| Parameter         |      Default Value      |                                         Notes |
|-------------------|:-----------------------:|----------------------------------------------:|
| http.host         |        localhost        |                  http host to send request to |
| http.port         |          8080           |                  http port to send request to |
| upload.path       | /servlet-example/upload |                               the upload path |
| short.param       |          text           |                  a short http query parameter |
| short.param.value |   example short text    |            a short http query parameter value |
| file.param        |          long           |          a file parameter name to be uploaded |
| file.name         |           ''            |             the file name used to be uploaded |
| file.path         |           ''            | the file path used to be uploaded. (required) |
| file.content.type |    application/octet    |                         the file content type |