package org.ai.integration.types;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@ApplicationScoped
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HistoryFormat {
    String prompt;
    String aiAnswer;
}
