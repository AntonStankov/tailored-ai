package org.ai.utils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.ai.config.ApplicationConfig;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class HistoryUtils {

    private final ApplicationConfig applicationConfig;

    public String formatHistory(String prompt, String answer) {
        return applicationConfig.historyStarter() + prompt + applicationConfig.historySeparator() + answer + applicationConfig.historyEnder();
    }
}
