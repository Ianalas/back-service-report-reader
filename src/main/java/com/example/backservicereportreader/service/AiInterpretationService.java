package com.example.backservicereportreader.service;

import com.example.backservicereportreader.strategy.InterpretationStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiInterpretationService {

    private final InterpretationStrategy strategy;

    public AiInterpretationService(InterpretationStrategy strategy) {
        this.strategy = strategy;
    }

    public String summarizePdfText(List<String> lines) {
        return strategy.interpret(lines);
    }
}

