package org.ai.service;

import io.quarkus.elytron.security.common.BcryptUtil;
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
        client.setUsername(client.getName());
        client.setPassword(BcryptUtil.bcryptHash(client.getName()));
        client.setRole("client1_" + client.getName());

        clientRepo.persist(client);
        clientRepo.flush();
        return client;
    }

    @Transactional
    public Client addAiInstructions(List<String> aiInstructions, Long clientId) {
        Client client = clientRepo.findById(clientId);
        client.getAiInstructions().addAll(aiInstructions);
        clientRepo.persist(client);
        return client;
    }

    @Transactional
    public Client deleteAiInstructions(List<String> aiInstructions, Long clientId) {
        Client client = clientRepo.findById(clientId);
        client.getAiInstructions().removeAll(aiInstructions);
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
