package org.ai.resource;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.SneakyThrows;
import org.ai.auth.ClientRoleAllowed;
import org.ai.config.ApplicationConfig;
import org.ai.integration.types.HistoryFormat;
import org.ai.integration.types.Records;
import org.ai.service.ClientServiceImpl;
import org.ai.service.HistoryService;
import org.ai.service.external.HistoryNginxClient;
import org.ai.service.external.NvidiaServiceClient;
import org.ai.utils.HistoryUtils;
import org.ai.utils.NvidiaUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("/chat")
public class NvidiaResource {

    @Inject
    @RestClient
    private NvidiaServiceClient nvidiaServiceClient;

    @Inject
    private ApplicationConfig applicationConfig;

    @Inject
    private NvidiaUtils nvidiaUtils;

    @Inject
    private SecurityIdentity securityIdentity;

    @Inject
    private ClientServiceImpl clientService;

    @Inject
    private HistoryService historyService;

    @Inject
    private HistoryUtils historyUtils;

    @Inject
    @RestClient
    private HistoryNginxClient historyNginxClient;

    @POST
    @SneakyThrows
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

        String aiAnswer = nvidiaServiceClient.completion(nvidiaUtils.getBearer(),
                        Records.NvidiaAIRequest.newRequest(
                                applicationConfig.openAiModel(),
                                applicationConfig.promptScript() + instructions + applicationConfig.promptQuestion() + prompt + applicationConfig.promptFinal()))
                .choices().toString();

        historyService.writeToFileInContainer(applicationConfig.nginxAddress(),
                applicationConfig.nginxPort(),
                applicationConfig.nginxUsername(),
                applicationConfig.nginxPassword(),
                applicationConfig.nginxDir() + username + applicationConfig.historyFileSuffix(),
                historyUtils.formatHistory(prompt, aiAnswer));

        clientService.increasePromptSentByUsername(username);

        return aiAnswer;
    }

    @GET
    @SneakyThrows
    @ClientRoleAllowed
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getHistory")
    public List<HistoryFormat> getHistory() {
        String username = securityIdentity.getPrincipal().getName();
        String historyString = historyNginxClient.getFileContent(username + applicationConfig.historyFileSuffix());
        List<HistoryFormat> history = new ArrayList<>();

        Arrays.stream(historyString.split(applicationConfig.historyEnder())).forEach(action -> {
            HistoryFormat historyFormat = new HistoryFormat();
            if (!action.isBlank()){
                List<String> str = Arrays.stream(Arrays.stream(action.split(applicationConfig.historyStarter())).toList().get(1).split(applicationConfig.historySeparator())).toList();

                historyFormat.setPrompt(str.get(0));
                historyFormat.setAiAnswer(str.get(1).split(applicationConfig.assistantCutStart())[1].split(applicationConfig.assistantCutEnd())[0]);

                history.add(historyFormat);
            }
        });

        return history;
    }
}
