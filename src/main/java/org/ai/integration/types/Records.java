package org.ai.integration.types;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class Records {
    public record ChatGptMessage(String role, String content) {
        public ChatGptMessage {
            if (role == null || role.isBlank()) {
                throw new IllegalArgumentException("Role must not be null or blank");
            }
            if (content == null || content.isBlank()) {
                throw new IllegalArgumentException("Content must not be null or blank");
            }
        }
    }

    public record ChatGptRequest(String model, List<ChatGptMessage> messages) {

        public static ChatGptRequest newRequest(String model, String prompt) {
            final List<ChatGptMessage> messages = new ArrayList<>();
            messages.add(new ChatGptMessage("user", prompt));
            return new ChatGptRequest(model, messages);
        }

        public ChatGptRequest {
            if (model == null || model.isBlank()) {
                throw new IllegalArgumentException("Model must not be null or blank");
            }
            if (messages == null || messages.isEmpty()) {
                throw new IllegalArgumentException("Messages must not be null or empty");
            }
        }
    }

    public record ChatGptChoice(int index, ChatGptMessage message) {
        public ChatGptChoice {
            if (index < 0) {
                throw new IllegalArgumentException("Index must be greater than or equal to 0");
            }
            if (message == null) {
                throw new IllegalArgumentException("Message must not be null");
            }
        }
    }

    public record ChatGptResponse(List<ChatGptChoice> choices) {
        public ChatGptResponse {
            if (choices == null || choices.isEmpty()) {
                throw new IllegalArgumentException("Choices must not be null or empty");
            }
        }
    }


}
