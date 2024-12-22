package org.ai.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ai.config.ApplicationConfig;
import org.ai.entity.Client;
import org.ai.entity.HistoryEntry;
import org.ai.service.repository.HistoryRepo;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class HistoryService {

    private final ApplicationConfig applicationConfig;

    private final HistoryRepo historyRepo;


    @Transactional
    public void saveHistory(HistoryEntry historyEntry) {
        historyRepo.persist(historyEntry);
        historyRepo.flush();
    }
    public void deleteHistory(Long id) {
        historyRepo.deleteById(id);
    }

    public void deleteHistoryByClientId(Long clientId) {
        historyRepo.delete("client.id", clientId);
    }

    public void deleteAllHistory() {
        historyRepo.deleteAll();
    }

    public List<HistoryEntry> getHistoryByClient(Client client) {
        return historyRepo.findByClient(client);
    }
}
