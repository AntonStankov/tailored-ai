package org.ai.resource;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.SneakyThrows;
import org.ai.auth.ClientRoleAllowed;
import org.ai.config.ApplicationConfig;
import org.ai.entity.PrivateClient;
import org.ai.integration.types.CompletionRequest;
import org.ai.integration.types.GenericAuthRequest;
import org.ai.integration.types.HistoryFormat;
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

import static java.lang.Thread.sleep;

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
        String username = securityIdentity.getPrincipal().getName();
        Map<String, Object> threadResponse = openAIAssistantClient.createThread();
        String threadId = (String) threadResponse.get("id");

        privateClientService.addThread(threadId, username);

        return Map.of("thread_id", threadId);
    }

    @POST
    @SneakyThrows
    @ClientRoleAllowed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Bulkhead(value = 20, waitingTaskQueue = 30)
    @Path("/send")
    public String useAssistantConversation(CompletionRequest completionRequest) {
        String username = securityIdentity.getPrincipal().getName();

        String threadId = (String) completionRequest.getThreadId();

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", completionRequest.getPrompt());
        openAIAssistantClient.addMessage(threadId, message);

        Map<String, Object> runParams = new HashMap<>();
        runParams.put("assistant_id", privateClientService.getPrivateClientByClient(clientService.findByUsername(username)).getAssistantId());
        try {
            Map<String, Object> assistantResponse = openAIAssistantClient.startRun(threadId, runParams);
            System.out.println("Assistant run started successfully: " + assistantResponse);
        } catch (WebApplicationException e) {
            String errorMessage = e.getResponse().readEntity(String.class);
            System.out.println("Error details: " + errorMessage);  // Print detailed error response
            throw e;  // Optionally, you can throw or handle differently
        }
        sleep(5000);
        Map<String, Object> response = openAIAssistantClient.getMessages(threadId);
        Object data = response.get("data");


        List<Map<String, Object>> messages = null;
        if (data instanceof List<?>) {
            List<?> dataList = (List<?>) data;
            if (!dataList.isEmpty() && dataList.get(0) instanceof Map) {
                messages = (List<Map<String, Object>>) dataList;
            } else {
                System.out.println("Invalid data structure");
            }
        }
        if (messages != null) {
            String aiResponse = "";
            for (Map<String, Object> msg : messages) {
                if ("assistant".equals(msg.get("role"))) {
                    Object content = msg.get("content");

                    if (content instanceof String) {
                        // If it's a string, simply cast it
                        aiResponse = (String) content;
                    } else if (content instanceof List<?>) {
                        // If it's a list, you might want to convert it to a string or handle differently
                        aiResponse = content.toString();  // Or some custom logic to handle the list
                    } else {
                        aiResponse = "Unexpected content type: " + content.getClass().getName();
                    }
                    break;
                }
            }
//            TODO: fails when its big data(its possible that we don't need it anymore) historyService.saveHistory(new HistoryEntry(null, completionRequest.getPrompt(), aiResponse, clientService.findByUsername(username)));
            clientService.increasePromptSentByUsername(username);
            return aiResponse;
        } else {
            throw new WebApplicationException("There are no messages!", 400);
        }




    }



//    @GET
//    @SneakyThrows
//    @ClientRoleAllowed
//    @Produces(MediaType.APPLICATION_JSON)
//    @Bulkhead(value = 10, waitingTaskQueue = 20)
//    @Path("/getHistory")
//    public List<Thread> getHistory(GenericAuthRequest<String> request) {
//        String username = securityIdentity.getPrincipal().getName();
////        if (!privateClientService.checkPrivateClientAuthority(request.getUsername(), request.getPassword(), username)) {
////            throw new ForbiddenException();
////        }
////
////        String historyString = historyNginxClient.getFileContent(username + applicationConfig.historyFileSuffix());
////        List<HistoryFormat> history = new ArrayList<>();
////
////        Arrays.stream(historyString.split(applicationConfig.historyEnder())).forEach(action -> {
////            HistoryFormat historyFormat = new HistoryFormat();
////            if (!action.isBlank()){
////                List<String> str = Arrays.stream(Arrays.stream(action.split(applicationConfig.historyStarter())).toList().get(1).split(applicationConfig.historySeparator())).toList();
////
////                historyFormat.setPrompt(str.get(0));
////                historyFormat.setAiAnswer(str.get(1).split(applicationConfig.assistantCutStart())[1].split(applicationConfig.assistantCutEnd())[0]);
////
////                history.add(historyFormat);
////            }
////        });
////
////        return history;
//        PrivateClient privateClient = privateClientService.getPrivateClientByClient(clientService.findByUsername(username));
//        List<String> threadsIds = privateClient.getThreadIds();
//
//
//
//
//        List<Thread> threads = new ArrayList<>();
//        for (String threadId : threadsIds) {
//            Map<String, Object> response = openAIAssistantClient.getMessages(threadId);
//            Object data = response.get("data");
//            List<Map<String, Object>> messages = null;
//            if (data instanceof List<?>) {
//                List<?> dataList = (List<?>) data;
//                if (!dataList.isEmpty() && dataList.get(0) instanceof Map) {
//                    messages = (List<Map<String, Object>>) dataList;
//                } else {
//                    System.out.println("Invalid data structure");
//                }
//            }
//            if (messages != null) {
//                List<Message> messagesList = new ArrayList<>();
//                for (Map<String, Object> msg : messages) {
//                    Object content = msg.get("content");
//                    if (content instanceof String) {
//                        messagesList.add(new Message((String) msg.get("role"), (String) content));
//                    }
//                }
//                threads.add(new Thread(messagesList));
//            } else {
//                throw new WebApplicationException("There are no messages!", 400);
//            }
//        }
//        return threads;
//    }
}
