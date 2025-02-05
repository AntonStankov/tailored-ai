package org.ai.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ai.entity.Client;
import org.ai.entity.PrivateClient;
import org.ai.service.repository.PrivateClientRepo;

import java.util.ArrayList;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PrivateClientService {

    private final PrivateClientRepo privateClientRepo;
    private final ClientServiceImpl clientService;

    @Transactional
    public PrivateClient savePrivateClient(Client client, String username, String password, String assistantId) {
        clientService.createClient(client);
        PrivateClient privateClient = new PrivateClient();
        privateClient.setClient(client);
        privateClient.setPrivateUsername(username);
        privateClient.setPrivatePassword(BcryptUtil.bcryptHash(password));
        privateClient.setAssistantId(assistantId);
        privateClient.setThreadIds(new ArrayList<>(0));
        privateClientRepo.persist(privateClient);
        privateClientRepo.flush();
        return privateClient;
    }

    public PrivateClient getPrivateClientByClient(Client client) {
        return privateClientRepo.find("client", client).firstResult();
    }

    public boolean checkPrivateClientAuthority(String username, String password, String publicUsername) {
        if (!username.equals(publicUsername)) {
            return false;
        }
        Client client = clientService.findByUsername(username);
        PrivateClient privateClient = getPrivateClientByClient(client);
        return username.equals(privateClient.getPrivateUsername()) && BcryptUtil.matches(password, privateClient.getPrivatePassword());
    }
    @Transactional
    public boolean addThread(String threadId, String username) {
        Client client = clientService.findByUsername(username);
        PrivateClient privateClient = getPrivateClientByClient(client);
        if (privateClient.getThreadIds().contains(threadId)) {
            return false;
        }
        privateClient.getThreadIds().add(threadId);
        privateClientRepo.persist(privateClient);
        privateClientRepo.flush();
        return true;
    }
}
