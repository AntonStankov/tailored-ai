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

    @WithName("prompt.script")
    String promptScript();

    @WithName("prompt.question")
    String promptQuestion();

    @WithName("prompt.final")
    String promptFinal();

    @WithName("history.file.suffix")
    String historyFileSuffix();

    @WithName("nginx.dir")
    String nginxDir();

    @WithName("nginx.address")
    String nginxAddress();

    @WithName("nginx.port")
    int nginxPort();

    @WithName("nginx.user")
    String nginxUsername();

    @WithName("nginx.password")
    String nginxPassword();

    @WithName("history.separator")
    String historySeparator();

    @WithName("history.ender")
    String historyEnder();

    @WithName("history.starter")
    String historyStarter();

    @WithName("assistant.cut.start")
    String assistantCutStart();

    @WithName("assistant.cut.end")
    String assistantCutEnd();

    @WithName("request.filter")
    Integer requestFilter();
}
