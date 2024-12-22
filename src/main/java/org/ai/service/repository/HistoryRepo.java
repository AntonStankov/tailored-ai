package org.ai.service.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.ai.entity.Client;
import org.ai.entity.HistoryEntry;

import java.util.List;

@ApplicationScoped
public class HistoryRepo implements PanacheRepositoryBase<HistoryEntry, Long> {
    public List<HistoryEntry> findByClient(Client client) {
        return list("client", client);
    }
}
