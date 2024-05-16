package io.quarkus.demos.ai;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.image.ImageModel;
import io.quarkus.resteasy.reactive.jackson.CustomSerialization;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/llm")
public class LLMResource {

    // this should be the one to OpenAI
    @Inject
    private ImageModel imageModel;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST for LLM";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/image")
    @CustomSerialization(value = OpenAIImageSerialization.class)
    public List<Image> images(@QueryParam("prompt") String prompt) {
        List<Image> images = imageModel.generate(prompt, 1).content();
        return images;
    }

}
