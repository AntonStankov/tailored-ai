package org.ai.utils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.ai.config.ApplicationConfig;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class OpenAiUtils {

    private final ApplicationConfig applicationConfig;

    public String getBearer() {
        return "Bearer " + applicationConfig.openAiApiKey();
    }
}
