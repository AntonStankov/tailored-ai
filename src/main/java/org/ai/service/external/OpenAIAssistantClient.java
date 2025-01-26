package org.ai.service.external;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jboss.resteasy.reactive.MultipartForm;
import org.jboss.resteasy.reactive.server.core.multipart.FormData;

import java.io.InputStream;
import java.util.Map;


@RegisterRestClient(baseUri = "https://api.openai.com/v1")
@ClientHeaderParam(name = "Authorization", value = "Bearer ${openai.api.key}")
@ClientHeaderParam(name = "Content-Type", value = "application/json")
@ClientHeaderParam(name = "OpenAI-Beta", value = "assistants=v2")
public interface OpenAIAssistantClient {


    @POST
    @Path("/assistants")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Object> createAssistant(Map<String, Object> assistantRequest);

//    @POST
//    @Path("/files")
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces(MediaType.APPLICATION_JSON)
//    Map<String, Object> uploadFile(@FormDataParam("file") InputStream fileInputStream,
//                                   @FormDataParam("file") FormDataContentDisposition fileDetail);

    @POST
    @Path("/threads")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Object> createThread();

    @POST
    @Path("/threads/{thread_id}/messages")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Object> addMessage(@PathParam("thread_id") String threadId, Map<String, Object> message);

    @POST
    @Path("/threads/{thread_id}/runs")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Object> startRun(@PathParam("thread_id") String threadId, Map<String, Object> runParams);

    @GET
    @Path("/threads/{thread_id}/messages")
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Object> getMessages(@PathParam("thread_id") String threadId);
}
