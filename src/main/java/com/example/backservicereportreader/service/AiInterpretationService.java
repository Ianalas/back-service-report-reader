package com.example.backservicereportreader.service;

import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiInterpretationService {

    private final OllamaChatClient chatClient;

    public AiInterpretationService(OllamaChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String summarizePdfText(List<String> lines) {
        String fullText = String.join("\n", lines);

        String prompt = "Lembre-se que vc é um especialista em medicina que deverá " +
                "resumir de forma fácil a paritr de dados passados abaixo, o que está sendo falado naqueles dados mostrados," +
                "e por fim divida em dois tópicos (Explicação e conclusão):\n\n" + fullText;

        return chatClient.call(prompt);
    }
}
