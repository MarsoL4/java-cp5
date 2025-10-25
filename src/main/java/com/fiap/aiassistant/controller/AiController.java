package com.fiap.aiassistant.controller;

import com.fiap.aiassistant.service.AiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AiController {

    private final AiService aiService;

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

        String result = aiService.handle(theme, inputText);

        model.addAttribute("theme", theme);
        model.addAttribute("inputText", inputText);
        model.addAttribute("result", result);
        return "result";
    }
}