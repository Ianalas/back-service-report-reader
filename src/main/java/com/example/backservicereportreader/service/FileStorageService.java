package com.example.backservicereportreader.service;

import com.example.backservicereportreader.config.FileStorageProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private final FileScannerService fileScannerService;

    public FileStorageService(FileStorageProperties fileStorageProperties, FileScannerService fileScannerService) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        this.fileScannerService = fileScannerService;

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar o diretório de upload.", e);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        file.transferTo(targetLocation);

        try { //Serviço de transformação
            fileScannerService.transformFilesToText(targetLocation.toFile());
        } catch (Exception e) {
            System.out.println("Erro ao transformar PDF em texto: " + e.getMessage());
        }


        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/file/download/")
                .path(fileName)
                .toUriString();
    }

    public Resource loadFileAsResource(String fileName) throws MalformedURLException {
        Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
        return new UrlResource(filePath.toUri());
    }

    public List<String> listAllFiles() throws IOException {
        return Files.list(fileStorageLocation)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
    }
}

