package org.ai.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "app")
public interface ApplicationConfig {
    @WithName("nvidia.key")
    String openAiApiKey();

    @WithName("nvidia.model")
    String openAiModel();

    @WithName("admin.username")
    String adminUsername();
}
