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
import org.ai.service.ClientServiceImpl;

import java.util.List;

@Path("/clients")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ClientResource {

    private final ClientServiceImpl clientService;

    private final SecurityIdentity securityIdentity;

    @Path("/add")
    @POST
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Client addClient(Client client) {
        return clientService.createClient(client);
    }

    @Path("/delete/{id}")
    @POST
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteClient(@PathParam("id") Long id) {
        clientService.deleteClient(id);
    }

    @Path("/get/{id}")
    @POST
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Client getClient(@PathParam("id") Long id) {
        return clientService.getClient(id);
    }

    @Path("/get-all")
    @POST
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Client> getClients() {
        return clientService.getClients();
    }

    @Path("/add-ai-instructions")
    @POST
    @ClientRoleAllowed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Client addAiInstructions(List<String> aiInstructions) {
        String username = securityIdentity.getPrincipal().getName();
        return clientService.addAiInstructions(aiInstructions, username);
    }

    @Path("/delete-ai-instructions")
    @POST
    @ClientRoleAllowed
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Client deleteAiInstructions(List<String> aiInstructions) {
        String username = securityIdentity.getPrincipal().getName();
        return clientService.deleteAiInstructions(aiInstructions, username);
    }

    @Path("/get-instructions")
    @POST
    @ClientRoleAllowed
    @Produces(MediaType.APPLICATION_JSON)
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
    public Client findMe(){
        return clientService.findByUsername(securityIdentity.getPrincipal().getName());
    }
}