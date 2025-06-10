package com.example.backservicereportreader.strategy;

import java.util.List;

public interface InterpretationStrategy {
    String interpret(List<String> lines);
}
