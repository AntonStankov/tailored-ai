package org.ai.integration.types;

import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ApplicationScoped
public class CompletionRequest {
    private String prompt;
    private List<Records.NvidiaAIMessage> messages;
}
