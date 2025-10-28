package com.fiap.aiassistant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class OpenAiClient {

    private final WebClient webClient;
    private final String apiKey;
    private final String model;

    public OpenAiClient(
            @Value("${spring.ai.openai.api-key:}") String apiKey,
            @Value("${spring.ai.openai.chat.options.model:gpt-3.5-turbo}") String model) {
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.model = model != null ? model.trim() : "gpt-3.5-turbo";
        this.webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeaders(h -> {
                    if (!this.apiKey.isEmpty()) {
                        h.setBearerAuth(this.apiKey);
                    }
                    h.setContentType(MediaType.APPLICATION_JSON);
                })
                .build();
    }

    /**
     * Envia um prompt ao endpoint de chat completions e retorna o texto respondido.
     * Lança RuntimeException em caso de erro de rede ou API.
     */
    public String sendPrompt(String prompt) {
        if (apiKey.isEmpty()) {
            throw new IllegalStateException("OpenAI API key não configurada.");
        }

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.2
        );

        Map<String, Object> response = webClient.post()
                .uri("/chat/completions")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                          clientResponse -> extractErrorMessage(clientResponse))
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .timeout(Duration.ofSeconds(30)) // Timeout configurável
                .block();

        if (response == null) {
            throw new RuntimeException("Resposta vazia da API");
        }
        Object choicesObj = response.get("choices");
        if (choicesObj instanceof List && !((List<?>) choicesObj).isEmpty()) {
            Object first = ((List<?>) choicesObj).get(0);
            if (first instanceof Map) {
                Object message = ((Map<?, ?>) first).get("message");
                if (message instanceof Map) {
                    Object content = ((Map<?, ?>) message).get("content");
                    if (content != null) {
                        return content.toString().trim();
                    }
                }
                Object text = ((Map<?, ?>) first).get("text");
                if (text != null) {
                    return text.toString().trim();
                }
            }
        }
        throw new RuntimeException("Formato inesperado de resposta da OpenAI: " + response);
    }

    private Mono<Throwable> extractErrorMessage(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
                .defaultIfEmpty("Erro desconhecido ao chamar OpenAI")
                .map(body -> new RuntimeException("OpenAI API retornou status " + clientResponse.statusCode() + ": " + body));
    }
}