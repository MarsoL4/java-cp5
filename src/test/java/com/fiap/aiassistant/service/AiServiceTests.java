package com.fiap.aiassistant.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiServiceTests {

    @Test
    void buildPrompt_forTheme1_includesJUnitMention() {
        AiService service = new AiService(null, "");
        String input = "public class Foo { public int add(int a, int b){ return a+b; } }";
        String prompt = service.buildPrompt("1", input);
        assertThat(prompt).contains("JUnit 5").contains("Classe Java").contains("public class Foo");
    }

    @Test
    void buildPrompt_forTheme2_mentionsTranslate() {
        AiService service = new AiService(null, "");
        String input = "Olá, como você está?";
        String prompt = service.buildPrompt("2", input);
        assertThat(prompt).contains("tradutor ético").contains("português para o espanhol").contains("Olá, como você está?");
    }

    @Test
    void buildPrompt_forTheme3_mentionsImprovements() {
        AiService service = new AiService(null, "");
        String input = "public class Bar { }";
        String prompt = service.buildPrompt("3", input);
        assertThat(prompt).contains("analisa código Java").contains("sugere melhorias").contains("public class Bar");
    }

    @Test
    void handle_withoutApiKey_returnsPrompt() {
        AiService service = new AiService(null, "");
        String input = "class X {}";
        String response = service.handle("1", input);
        assertThat(response).contains("OpenAI API key não configurada");
        assertThat(response).contains("Classe Java");
    }
}