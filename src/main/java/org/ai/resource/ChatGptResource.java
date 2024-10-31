package org.ai.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.ai.auth.ClientRoleAllowed;
import org.ai.config.ApplicationConfig;
import org.ai.integration.types.Records;
import org.ai.service.external.ChatGptServiceClient;
import org.ai.utils.OpenAiUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/chat")
public class ChatGptResource {

    @Inject
    @RestClient
    private ChatGptServiceClient chatGptServiceClient;

    @Inject
    private ApplicationConfig applicationConfig;

    @Inject
    private OpenAiUtils openAiUtils;

    @POST
    @ClientRoleAllowed
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String completion(String prompt) {
        return chatGptServiceClient.completion(openAiUtils.getBearer(),
                        Records.ChatGptRequest.newRequest(applicationConfig.openAiModel(), prompt))
                .choices().toString();
    }
}
