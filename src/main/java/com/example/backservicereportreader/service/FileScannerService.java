package com.example.backservicereportreader.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class FileScannerService {

    private final AiInterpretationService aiService;

    public FileScannerService(AiInterpretationService aiService) {
        this.aiService = aiService;
    }

    public String transformFilesToText(File file) throws Exception {
        PDDocument document = Loader.loadPDF(file);

        PDFTextStripper stripper = new PDFTextStripper();
        List<String> lines = List.of(stripper.getText(document).split("\n"));
        document.close();

        return aiService.summarizePdfText(lines);
    }
}