package org.ai.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.ai.entity.Client;
import org.ai.service.ClientServiceImpl;

import java.util.List;

@Path("/clients")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ClientResource {

    private final ClientServiceImpl clientService;

    @Path("/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Client addClient(Client client) {
        return clientService.createClient(client);
    }

    @Path("/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Client updateClient(Client client) {
        return clientService.updateClient(client);
    }

    @Path("/delete/{id}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteClient(@PathParam("id") Long id) {
        clientService.deleteClient(id);
    }

    @Path("/get/{id}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Client getClient(@PathParam("id") Long id) {
        return clientService.getClient(id);
    }

    @Path("/get-all")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public List<Client> getClients() {
        return clientService.getClients();
    }
}
