package org.ai.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ai.entity.Client;
import org.ai.service.repository.ClientRepo;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ClientServiceImpl{
    private final ClientRepo clientRepo;


    @Transactional
    public Client createClient(Client client) {
        clientRepo.persist(client);
        clientRepo.flush();
        return client;
    }

    @Transactional
    public Client updateClient(Client client) {
        clientRepo.persist(client);
        return client;
    }

    @Transactional
    public void deleteClient(Long id) {
        clientRepo.deleteById(id);
    }

    public Client getClient(Long id) {
        return clientRepo.findById(id);
    }

    public List<Client> getClients() {
        return clientRepo.listAll();
    }
}
