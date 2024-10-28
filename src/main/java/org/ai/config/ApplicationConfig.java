package org.ai.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "app")
public interface ApplicationConfig {
    @WithName("openai.key")
    String openAiApiKey();

    @WithName("openai.model")
    String openAiModel();
}
