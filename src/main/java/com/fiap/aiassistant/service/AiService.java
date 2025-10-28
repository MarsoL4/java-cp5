package com.fiap.aiassistant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AiService {

    private final OpenAiClient openAiClient;
    private final String apiKey;

    public AiService(OpenAiClient openAiClient, @Value("${spring.ai.openai.api-key:}") String apiKey) {
        this.openAiClient = openAiClient;
        this.apiKey = apiKey != null ? apiKey.trim() : "";
    }

    public String handle(String theme, String input) {
        String prompt = buildPrompt(theme, input);
        if (!StringUtils.hasText(apiKey)) {
            // Sem chave: retorna o prompt para desenvolvimento / testes
            return "OpenAI API key não configurada. Prompt que seria enviado ao modelo:\n\n" + prompt;
        }
        try {
            return openAiClient.sendPrompt(prompt);
        } catch (Exception ex) {
            return "Erro ao chamar OpenAI: " + ex.getMessage();
        }
    }

    String buildPrompt(String theme, String input) {
        String trimmed = input == null ? "" : input.trim();
        switch (theme) {
            case "1":
                return "Você é um assistente experiente em Java que gera testes unitários com JUnit 5. "
                        + "Gere testes unitários para a classe Java a seguir. Use boas práticas de teste (arrange-act-assert), "
                        + "mocagem mínima necessária e mostre apenas o código dos testes dentro de uma classe de teste. "
                        + "Se necessário, adicione comentários curtos explicando as escolhas.\n\nClasse Java:\n" + trimmed;
            case "2":
                return "Você é um tradutor ético e moral. Traduza do português para o espanhol mantendo o sentido, "
                        + "respeitando o contexto e evitando linguagem ofensiva ou que incentive comportamento antiético. "
                        + "Se existir linguagem problemática, adapte-a de forma responsável.\n\nTexto em português:\n" + trimmed;
            case "3":
                return "Você é um assistente que analisa código Java e sugere melhorias. "
                        + "Identifique problemas, antipadrões, riscos de concorrência, possíveis bugs, melhorias de legibilidade, "
                        + "possível refatoração e proponha versões corrigidas/trechos de código quando apropriado. Seja claro e objetivo.\n\nCódigo Java:\n" + trimmed;
            default:
                return "Tema desconhecido. Forneça 1, 2 ou 3.";
        }
    }
}