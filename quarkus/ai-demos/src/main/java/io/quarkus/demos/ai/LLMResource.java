package io.quarkus.demos.ai;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import io.quarkus.resteasy.reactive.jackson.CustomSerialization;
import jakarta.inject.Inject;
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
    @Path("/chat")
    public String chat(String message) {
        return chatter.chat(message);
    }

}
