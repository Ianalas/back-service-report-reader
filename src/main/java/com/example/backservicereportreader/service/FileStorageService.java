package com.example.backservicereportreader.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.backservicereportreader.Repository.ResultRepository;
import com.example.backservicereportreader.config.FileStorageProperties;
import com.example.backservicereportreader.dto.AiResultDTO;
import com.example.backservicereportreader.entity.AiResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${aws.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3Client;
    private final FileScannerService fileScannerService;
    private final ResultRepository summaryRepository;

    public FileStorageService(AmazonS3 s3Client, FileScannerService fileScannerService, ResultRepository summaryRepository) {
        this.s3Client = s3Client;
        this.fileScannerService = fileScannerService;
        this.summaryRepository = summaryRepository;
    }


    public AiResultDTO storeFile(MultipartFile multipartFile, UUID userID) throws IOException {
        String fileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        File localFile = null;
        try {
            localFile = convertMultipartToFile(multipartFile);

            // Transforma PDF em texto
            String summarized = fileScannerService.transformFilesToText(localFile);
            System.out.println("Resumo gerado: " + summarized);

            // Faz o upload do arquivo original para o S3
            s3Client.putObject(bucketName, fileName, localFile);
            String fileUrl = s3Client.getUrl(bucketName, fileName).toString();


            AiResult summary = new AiResult();
            summary.setTitle(multipartFile.getOriginalFilename());
            summary.setContent(summarized);
            summary.setUserId(userID);

            //summary.setFileUrl(fileUrl); // salvar a URL no db
            System.out.println("url do pdf"+fileUrl);

            AiResult saved = summaryRepository.save(summary);

            return new AiResultDTO(
                    saved.getId(),
                    saved.getContent(),
                    saved.getUserId(),
                    saved.getCreatedAt(),
                    saved.getTitle()
            );

        } catch (Exception e) {
            System.out.println("Erro ao processar o arquivo: " + e.getMessage());
            throw new RuntimeException("Erro ao processar o arquivo", e);
        } finally {
            if (localFile != null && localFile.exists()) {
                localFile.delete(); // limpa o arquivo local
            }
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

    public AiResultDTO findReportById(UUID reportId) {
        return summaryRepository.findById(reportId)
                .map(result -> new AiResultDTO(
                        result.getId(),
                        result.getContent(),
                        result.getUserId(),
                        result.getCreatedAt(),
                        result.getTitle()
                ))
                .orElseThrow(() -> new IllegalArgumentException("Laudo com ID " + reportId + " não encontrado."));
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

    private File convertMultipartToFile(MultipartFile multipartFile) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID() + "-" + multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        return convFile;
    }

}

