package org.ai.service.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.ai.entity.Client;


@ApplicationScoped
public class ClientRepo implements PanacheRepository<Client> {

}
