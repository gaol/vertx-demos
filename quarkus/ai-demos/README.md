# ai-demos

This is a Quarkus demo app, created using command:

> quarkus create app --maven --java=17 --package-name=io.quarkus.demos.ai --extensions=langchain4j-ollama,langchain4j-openai,quarkus-resteasy-reactive,uarkus-resteasy-reactive-jackson io.quarkus.demos:ai-demos:0.0.1-SNAPSHOT


## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Start redis for embedding store

> docker run --rm -d --name redis-stack -v $(pwd)/local-data/:/data -p 6379:6379 redis/redis-stack:7.2.0-v10-x86_64

