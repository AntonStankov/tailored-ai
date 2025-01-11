package org.ai.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ai.entity.Client;
import org.ai.entity.PrivateClient;
import org.ai.service.repository.PrivateClientRepo;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class PrivateClientService {

    private final PrivateClientRepo privateClientRepo;
    private final ClientServiceImpl clientService;

    @Transactional
    public PrivateClient savePrivateClient(Client client, String username, String password) {
        clientService.createClient(client);
        PrivateClient privateClient = new PrivateClient();
        privateClient.setClient(client);
        privateClient.setPrivateUsername(username);
        privateClient.setPrivatePassword(BcryptUtil.bcryptHash(password));
        privateClientRepo.persist(privateClient);
        privateClientRepo.flush();
        return privateClient;
    }
}
