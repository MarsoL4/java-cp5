package com.fiap.aiassistant.controller;

import com.fiap.aiassistant.service.AiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AiController {

    private final AiService aiService;
    private static final int MAX_INPUT_LENGTH = 30000; // ajuste conforme necessário

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    @PostMapping("/ask")
    public String ask(
            @RequestParam("theme") String theme,
            @RequestParam("inputText") String inputText,
            Model model) {

        String trimmed = inputText == null ? "" : inputText.trim();
        if (trimmed.isEmpty()) {
            model.addAttribute("theme", theme);
            model.addAttribute("inputText", inputText);
            model.addAttribute("result", "Por favor, forneça um texto ou código de entrada.");
            return "result";
        }
        if (trimmed.length() > MAX_INPUT_LENGTH) {
            model.addAttribute("theme", theme);
            model.addAttribute("inputText", inputText.substring(0, Math.min(2000, inputText.length())) + "\n... (texto muito longo)");
            model.addAttribute("result", "Entrada excede o tamanho máximo permitido (" + MAX_INPUT_LENGTH + " caracteres). Por favor, reduza o texto.");
            return "result";
        }

        String result = aiService.handle(theme, trimmed);

        model.addAttribute("theme", theme);
        model.addAttribute("inputText", trimmed);
        model.addAttribute("result", result);
        return "result";
    }
}