package io.quarkus.demos.ai;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import io.quarkus.resteasy.reactive.jackson.CustomSerialization;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/llm")
public class LLMResource {

    // the imageModel comes from
    @Inject
    private ImageModel imageModel;

    @Inject
    private Chatter chatter;

    @Inject
    private AskCodeLlama codeLlama;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST for LLM";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/image")
    @CustomSerialization(value = OpenAIImageSerialization.class)
    public List<Image> images(String message) {
        return imageModel.generate(message, 1).content();
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/ask")
    public String ask(JsonObject message) {
        System.out.println("\nUser is asking: " + message + "\n");
        return chatter.ask(message.getString("message"));
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/chat")
    public String chat(JsonObject message) {
        System.out.println("\nUser is chatting: " + message + "\n");
        return chatter.chat(message.getString("session"), message.getString("message"));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/product")
    public Product product(JsonObject message) {
        System.out.println("\nUser is extracting product information from : " + message + "\n");
        return chatter.analyze(message.getString("message"));
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/code")
    public String codeLlama(JsonObject message) {
        System.out.println("\nUser is asking for code assistance: " + message + "\n");
        return codeLlama.codeAssistant(message.getString("message"));
    }

}
