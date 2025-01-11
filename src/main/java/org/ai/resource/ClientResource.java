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
import org.ai.integration.types.HistoryFormat;
import org.ai.integration.types.SaveClientRequest;
import org.ai.service.ClientServiceImpl;
import org.ai.service.HistoryService;
import org.ai.service.PrivateClientService;
import org.eclipse.microprofile.faulttolerance.Bulkhead;

import java.util.List;

@Path("/clients")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ClientResource {

    private final ClientServiceImpl clientService;

    private final SecurityIdentity securityIdentity;

    private final HistoryService historyService;

    private final PrivateClientService privateClientService;

    @Path("/add")
    @POST
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Bulkhead(value = 5, waitingTaskQueue = 10)
    public PrivateClient addClient(SaveClientRequest request) {
        return privateClientService.savePrivateClient(request.getClient(), request.getClient().getName(), request.getPassword());
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
    public List<String> addAiInstructions(List<String> aiInstructions) {
        String username = securityIdentity.getPrincipal().getName();
        return clientService.addAiInstructions(aiInstructions, username).getAiInstructions();
    }

    @Path("/delete-ai-instructions")
    @POST
    @ClientRoleAllowed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Bulkhead(value = 5, waitingTaskQueue = 10)
    public List<String> deleteAiInstructions(List<String> aiInstructions) {
        String username = securityIdentity.getPrincipal().getName();
        return clientService.deleteAiInstructions(aiInstructions, username).getAiInstructions();
    }

    @Path("/get-instructions")
    @POST
    @ClientRoleAllowed
    @Produces(MediaType.APPLICATION_JSON)
    @Bulkhead(value = 5, waitingTaskQueue = 10)
    public List<String> getInstructions() {
        String username = securityIdentity.getPrincipal().getName();
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
    public Client findMe(){
        return clientService.findByUsername(securityIdentity.getPrincipal().getName());
    }

    @GET
    @Path("/history")
    @ClientRoleAllowed
    @Produces(MediaType.APPLICATION_JSON)
    @Bulkhead(value = 10, waitingTaskQueue = 20)
    public List<HistoryEntry> getHistory() {
        String username = securityIdentity.getPrincipal().getName();
        return historyService.getHistoryByClient(clientService.findByUsername(username));
    }
}