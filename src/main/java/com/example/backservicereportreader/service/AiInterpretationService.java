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

        String prompt = "Resuma o conteúdo a seguir:\n\n" + fullText;

        return chatClient.call(prompt);
    }
}
