package com.example.backservicereportreader.service;

import com.example.backservicereportreader.Repository.ResultRepository;
import com.example.backservicereportreader.config.FileStorageProperties;
import com.example.backservicereportreader.dto.AiResultDTO;
import com.example.backservicereportreader.entity.AiResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private final FileScannerService fileScannerService;
    private final ResultRepository summaryRepository;

    public FileStorageService(FileStorageProperties fileStorageProperties, FileScannerService fileScannerService, ResultRepository summaryRepository) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        this.fileScannerService = fileScannerService;
        this.summaryRepository = summaryRepository;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o diretório de upload.", e);
        }
    }

    public String storeFile(MultipartFile file, UUID userID) throws IOException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        file.transferTo(targetLocation);

        try {
            String summarized = fileScannerService.transformFilesToText(targetLocation.toFile());
            System.out.println("Resumo gerado: " + summarized);

            AiResult summary = new AiResult();
            summary.setTitle(file.getName());
            summary.setContent(summarized);
            summary.setUserId(userID);

            summaryRepository.save(summary);

           return summarized;
        } catch (Exception e) {
            System.out.println("Erro ao transformar PDF em texto: " + e.getMessage());
            return "Erro ao transformar PDF em texto: " + e.getMessage();
        }

    }

    @Transactional(readOnly = true)
    public List<AiResultDTO> findAllSummariesByUserId(UUID userId) {
        return summaryRepository.findAllByUserId(userId).stream()
                .map(result -> new AiResultDTO(
                        result.getId(),
                        result.getContent(),
                        result.getUserId(),
                        result.getCreatedAt(),
                        result.getTitle()
                ))
                .toList();
    }

    public void deleteById(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            if (!summaryRepository.existsById(uuid)) {
                throw new IllegalArgumentException("Entidade com ID " + id + " não encontrada.");
            }
            summaryRepository.deleteById(uuid);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar a entidade com ID " + id, e);
        }
    }
}

