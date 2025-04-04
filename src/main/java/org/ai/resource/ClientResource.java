package org.ai.resource;

import io.quarkus.security.PermissionsAllowed;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.ai.auth.ClientRoleAllowed;
import org.ai.entity.Client;
import org.ai.entity.HistoryEntry;
import org.ai.entity.PrivateClient;
import org.ai.integration.types.GenericAuthRequest;
import org.ai.integration.types.HistoryFormat;
import org.ai.integration.types.SaveClientRequest;
import org.ai.service.ClientServiceImpl;
import org.ai.service.HistoryService;
import org.ai.service.PrivateClientService;
import org.ai.service.external.OpenAIAssistantClient;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/clients")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ClientResource {

    private final ClientServiceImpl clientService;

    private final SecurityIdentity securityIdentity;

    private final HistoryService historyService;

    private final PrivateClientService privateClientService;

    @Inject
    @RestClient
    private OpenAIAssistantClient openAIAssistantClient;

    public record Message(String role, String content) {
    }
    public record Thread(List<Message> messages) {
    }

    @Path("/add")
    @POST
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Bulkhead(value = 5, waitingTaskQueue = 10)
    public PrivateClient addClient(SaveClientRequest request) {

        Map<String, Object> assistantRequest = new HashMap<>();
        assistantRequest.put("name", request.getClient().getName());
        assistantRequest.put("instructions", String.join(", ", request.getClient().getAiInstructions()));
        assistantRequest.put("model", "gpt-4-turbo");
        System.out.println(assistantRequest);
        Map<String, Object> assistantResponse = openAIAssistantClient.createAssistant(assistantRequest);
        return privateClientService.savePrivateClient(request.getClient(), request.getClient().getName(), request.getPassword(), (String) assistantResponse.get("id"));
    }

    @Path("/delete/{id}")
    @POST
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Bulkhead(value = 5, waitingTaskQueue = 10)
    public void deleteClient(@PathParam("id") Long id) {
        clientService.deleteClient(id);
    }

    @Path("/get/{id}")
    @POST
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Bulkhead(value = 5, waitingTaskQueue = 10)
    public Client getClient(@PathParam("id") Long id) {
        return clientService.getClient(id);
    }

    @Path("/get-all")
    @POST
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    @Bulkhead(value = 5, waitingTaskQueue = 10)
    public List<Client> getClients() {
        return clientService.getClients();
    }

    @Path("/add-ai-instructions")
    @POST
    @ClientRoleAllowed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Bulkhead(value = 5, waitingTaskQueue = 10)
    public List<String> addAiInstructions(GenericAuthRequest<List<String>> request) {
        String username = securityIdentity.getPrincipal().getName();
        if (!privateClientService.checkPrivateClientAuthority(request.getUsername(), request.getPassword(), username)) {
            throw new ForbiddenException();
        }
        return clientService.addAiInstructions(request.getData(), username).getAiInstructions();
    }

    @Path("/delete-ai-instructions")
    @POST
    @ClientRoleAllowed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Bulkhead(value = 5, waitingTaskQueue = 10)
    public List<String> deleteAiInstructions(GenericAuthRequest<List<String>> request) {
        String username = securityIdentity.getPrincipal().getName();
        if (!privateClientService.checkPrivateClientAuthority(request.getUsername(), request.getPassword(), username)) {
            throw new ForbiddenException();
        }
        return clientService.deleteAiInstructions(request.getData(), username).getAiInstructions();
    }

    @Path("/get-instructions")
    @POST
    @ClientRoleAllowed
    @Produces(MediaType.APPLICATION_JSON)
    @Bulkhead(value = 5, waitingTaskQueue = 10)
    public List<String> getInstructions(GenericAuthRequest<String> request) {
        String username = securityIdentity.getPrincipal().getName();
        if (!privateClientService.checkPrivateClientAuthority(request.getUsername(), request.getPassword(), username)) {
            throw new ForbiddenException();
        }
        return clientService.getClients()
                .stream()
                .filter(client -> client.getUsername().equals(username))
                .findFirst().get().getAiInstructions();
    }

    @GET
    @Path("/findMe")
    @ClientRoleAllowed
    @Produces(MediaType.APPLICATION_JSON)
    @Bulkhead(value = 5, waitingTaskQueue = 10)
    public Client findMe(GenericAuthRequest<String> request) {
        if (!privateClientService.checkPrivateClientAuthority(request.getUsername(),
                request.getPassword(),
                securityIdentity.getPrincipal().getName())) {
            throw new ForbiddenException();
        }
        return clientService.findByUsername(securityIdentity.getPrincipal().getName());
    }

    @GET
    @Path("/history")
    @ClientRoleAllowed
    @Produces(MediaType.APPLICATION_JSON)
    @Bulkhead(value = 10, waitingTaskQueue = 20)
    public List<Thread> getHistory(GenericAuthRequest<String> request) {
        String username = securityIdentity.getPrincipal().getName();
        if (!privateClientService.checkPrivateClientAuthority(request.getUsername(), request.getPassword(), username)) {
            throw new ForbiddenException();
        }
//        return historyService.getHistoryByClient(clientService.findByUsername(username));
        PrivateClient privateClient = privateClientService.getPrivateClientByClient(clientService.findByUsername(username));
        List<String> threadsIds = privateClient.getThreadIds();


        List<Thread> threads = new ArrayList<>();
        for (String threadId : threadsIds) {
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
                List<Message> messagesList = new ArrayList<>();
                for (Map<String, Object> msg : messages) {
                    Object content = msg.get("content");
                    if (content instanceof String) {
                        messagesList.add(new Message((String) msg.get("role"), (String) content));
                    } else if (content instanceof List<?>) {
                        messagesList.add(new Message((String) msg.get("role"), content.toString()));
                    } else {
                        messagesList.add(new Message((String) msg.get("role"), "Unexpected content type: " + content.getClass().getName()));
                    }
                }
                threads.add(new Thread(messagesList));
            } else {
                throw new WebApplicationException("There are no messages!", 400);
            }
        }
        return threads;
    }
}