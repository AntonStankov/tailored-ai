package org.ai.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ai.entity.Client;
import org.ai.service.repository.ClientRepo;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ClientServiceImpl{
    private final ClientRepo clientRepo;

    @Transactional
    public Client createClient(Client client) {
        client.setUsername(client.getName());
        System.out.println(Base64.getEncoder().encodeToString(client.getName().getBytes(StandardCharsets.UTF_8)));
        client.setPassword(BcryptUtil.bcryptHash(Base64.getEncoder().encodeToString(client.getName().getBytes(StandardCharsets.UTF_8))));
        client.setRole("client_" + client.getName());

        clientRepo.persist(client);
        clientRepo.flush();
        return client;
    }

    @Transactional
    public Client addAiInstructions(List<String> aiInstructions, String username) {
        Client client = clientRepo.find("username", username).firstResult();
        client.getAiInstructions().addAll(aiInstructions);
        clientRepo.persist(client);
        return client;
    }

    @Transactional
    public Client deleteAiInstructions(List<String> aiInstructions, String username) {
        Client client = clientRepo.find("username", username).firstResult();
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

    @Transactional
    public void increasePromptSentByUsername(String username) {
        Client client = clientRepo.find("username", username).firstResult();
        client.setPromptsSent(client.getPromptsSent() + 1);
        clientRepo.persist(client);
    }


    public Client findByUsername(String username) {
        return clientRepo.find("username", username).firstResult();
    }
}
