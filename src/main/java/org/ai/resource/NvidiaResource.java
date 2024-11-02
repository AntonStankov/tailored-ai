package org.ai.resource;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.ai.auth.ClientRoleAllowed;
import org.ai.config.ApplicationConfig;
import org.ai.entity.Client;
import org.ai.integration.types.Records;
import org.ai.service.ClientServiceImpl;
import org.ai.service.external.NvidiaServiceClient;
import org.ai.utils.OpenAiUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.stream.Collectors;

@Path("/chat")
public class NvidiaResource {

    @Inject
    @RestClient
    private NvidiaServiceClient nvidiaServiceClient;

    @Inject
    private ApplicationConfig applicationConfig;

    @Inject
    private OpenAiUtils openAiUtils;

    @Inject
    private SecurityIdentity securityIdentity;

    @Inject
    private ClientServiceImpl clientService;

    @POST
    @ClientRoleAllowed
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/completion")
    public String completion(String prompt) {
        String username = securityIdentity.getPrincipal().getName();
        String instructions = String.join(", ", clientService.getClients()
                .stream()
                .filter(client -> client.getUsername().equals(username))
                .findFirst().get().getAiInstructions());

        return nvidiaServiceClient.completion(openAiUtils.getBearer(),
                        Records.NvidiaAIRequest.newRequest(
                                applicationConfig.openAiModel(),
                                applicationConfig.promptScript() + instructions + applicationConfig.promptQuestion() + prompt))
                .choices().toString();
    }
}
