package org.ai.integration.types;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ai.entity.Client;

@ApplicationScoped
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SaveClientRequest {
    private Client client;
    private String password;
}
