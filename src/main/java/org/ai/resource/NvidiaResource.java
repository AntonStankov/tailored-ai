package org.ai.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.ai.auth.ClientRoleAllowed;
import org.ai.config.ApplicationConfig;
import org.ai.integration.types.Records;
import org.ai.service.external.NvidiaServiceClient;
import org.ai.utils.OpenAiUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/chat")
public class NvidiaResource {

    @Inject
    @RestClient
    private NvidiaServiceClient nvidiaServiceClient;

    @Inject
    private ApplicationConfig applicationConfig;

    @Inject
    private OpenAiUtils openAiUtils;

    @POST
    @ClientRoleAllowed
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/completion")
    public String completion(String prompt) {
        return nvidiaServiceClient.completion(openAiUtils.getBearer(),
                        Records.NvidiaAIRequest.newRequest(applicationConfig.openAiModel(), prompt))
                .choices().toString();
    }
}
