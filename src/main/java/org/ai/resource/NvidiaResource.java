package org.ai.resource;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.SneakyThrows;
import org.ai.auth.ClientRoleAllowed;
import org.ai.config.ApplicationConfig;
import org.ai.entity.HistoryEntry;
import org.ai.integration.types.CompletionRequest;
import org.ai.integration.types.GenericAuthRequest;
import org.ai.integration.types.HistoryFormat;
import org.ai.integration.types.Records;
import org.ai.service.ClientServiceImpl;
import org.ai.service.HistoryService;
import org.ai.service.PrivateClientService;
import org.ai.service.external.HistoryNginxClient;
import org.ai.service.external.OpenAIAssistantClient;
import org.ai.utils.HistoryUtils;
import org.ai.utils.NvidiaUtils;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.*;

@Path("/chat")
public class NvidiaResource {

    @Inject
    @RestClient
    private OpenAIAssistantClient openAIAssistantClient;

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

    @Inject
    private PrivateClientService privateClientService;

    @GET
    @SneakyThrows
    @ClientRoleAllowed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Bulkhead(value = 20, waitingTaskQueue = 30)
    @Path("/start")
    public Map<String, Object> startAssistantConversation() {
        Map<String, Object> threadResponse = openAIAssistantClient.createThread();
        String threadId = (String) threadResponse.get("id");

        return Map.of("thread_id", threadId);
    }

    @POST
    @SneakyThrows
    @ClientRoleAllowed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Bulkhead(value = 20, waitingTaskQueue = 30)
    @Path("/chat")
    public Map<String, Object> useAssistantConversation(CompletionRequest completionRequest) {
        String username = securityIdentity.getPrincipal().getName();

        String threadId = (String) completionRequest.getThreadId();

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", completionRequest.getPrompt());
        openAIAssistantClient.addMessage(threadId, message);

        Map<String, Object> runParams = new HashMap<>();
        runParams.put("assistant_id", privateClientService.getPrivateClientByClient(clientService.findByUsername(username)).getAssistantId());
        openAIAssistantClient.startRun(threadId, runParams);

        Map<String, Object> response = openAIAssistantClient.getMessages(threadId);
        List<Map<String, Object>> messages = (List<Map<String, Object>>) response.get("data");

        String aiResponse = "";
        for (Map<String, Object> msg : messages) {
            if ("assistant".equals(msg.get("role"))) {
                aiResponse = (String) msg.get("content");
                break;
            }
        }

        historyService.saveHistory(new HistoryEntry(null, completionRequest.getPrompt(), aiResponse, clientService.findByUsername(username)));
        clientService.increasePromptSentByUsername(username);

        return Map.of("assistant_response", aiResponse);
    }


    @GET
    @SneakyThrows
    @ClientRoleAllowed
    @Produces(MediaType.APPLICATION_JSON)
    @Bulkhead(value = 10, waitingTaskQueue = 20)
    @Path("/getHistory")
    public List<HistoryFormat> getHistory(GenericAuthRequest<String> request) {
        String username = securityIdentity.getPrincipal().getName();
        if (!privateClientService.checkPrivateClientAuthority(request.getUsername(), request.getPassword(), username)) {
            throw new ForbiddenException();
        }

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
