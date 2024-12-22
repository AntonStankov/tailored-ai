package org.ai.integration.types;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class Records {
    public record NvidiaAIMessage(String role, String content) {
        public NvidiaAIMessage {
            if (role == null || role.isBlank()) {
                throw new IllegalArgumentException("Role must not be null or blank");
            }
            if (content == null || content.isBlank()) {
                throw new IllegalArgumentException("Content must not be null or blank");
            }
        }
    }

    public record NvidiaAIRequest(String model, List<NvidiaAIMessage> messages) {

        public static NvidiaAIRequest newRequest(String model, String prompt, List<NvidiaAIMessage> messages) {
            messages.add(new NvidiaAIMessage("user", prompt));
            return new NvidiaAIRequest(model, messages);
        }

        public NvidiaAIRequest {
            if (model == null || model.isBlank()) {
                throw new IllegalArgumentException("Model must not be null or blank");
            }
            if (messages == null || messages.isEmpty()) {
                throw new IllegalArgumentException("Messages must not be null or empty");
            }
        }
    }

    public record NvidiaAIChoice(int index, NvidiaAIMessage message) {
        public NvidiaAIChoice {
            if (index < 0) {
                throw new IllegalArgumentException("Index must be greater than or equal to 0");
            }
            if (message == null) {
                throw new IllegalArgumentException("Message must not be null");
            }
        }
    }

    public record NvidiaAIResponse(List<NvidiaAIChoice> choices) {
        public NvidiaAIResponse {
            if (choices == null || choices.isEmpty()) {
                throw new IllegalArgumentException("Choices must not be null or empty");
            }
        }
    }


}
