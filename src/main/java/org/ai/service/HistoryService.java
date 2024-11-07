package org.ai.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.ai.config.ApplicationConfig;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class HistoryService {

    private final ApplicationConfig applicationConfig;
    
    @Inject
    CamelContext camelContext;

    public void writeToFileInContainer(String host, int port, String user, String password, String filePath, String content) {
        try {
            String sshUri = String.format("ssh://%s@%s:%d/%s?password=%s", user, host, port, filePath, password);

            String command = "echo \"" + content + "\" >> " + filePath;

            ProducerTemplate template = camelContext.createProducerTemplate();
            template.sendBody(sshUri, command);

            System.out.println("File written successfully in the container.");
        } catch (Exception e) {
            System.out.println("Failed to write to file in container.");
        }
    }
}
