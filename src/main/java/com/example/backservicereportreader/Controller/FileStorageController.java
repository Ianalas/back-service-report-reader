package com.example.backservicereportreader.Controller;

import com.example.backservicereportreader.dto.AiResultDTO;
import com.example.backservicereportreader.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/scanner")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId) {

        try {
            UUID userIdUUID = UUID.fromString(userId);
            String fileDownloadUri = fileStorageService.storeFile(file, userIdUUID);
            return ResponseEntity.ok(fileDownloadUri);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Falha ao salvar arquivo.");
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listByUserId(@RequestParam("userId") String userId) {
        try {
            UUID userIdUUID = UUID.fromString(userId);

            List<AiResultDTO> listResultByUserId = fileStorageService.findAllSummariesByUserId(userIdUUID);
            if (listResultByUserId.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(listResultByUserId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("UUID inválido.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao buscar os resultados." + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") String id) {
        try {
            fileStorageService.deleteById(id);
            return ResponseEntity.ok("Entidade deletada com sucesso.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno ao tentar deletar.");
        }
    }
}
