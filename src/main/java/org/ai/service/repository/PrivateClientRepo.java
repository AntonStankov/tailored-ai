package org.ai.service.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.ai.entity.Client;
import org.ai.entity.PrivateClient;

@ApplicationScoped
public class PrivateClientRepo implements PanacheRepository<PrivateClient> {
}
