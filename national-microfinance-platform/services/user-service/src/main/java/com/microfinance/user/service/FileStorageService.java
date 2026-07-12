package com.microfinance.user.service;

import com.microfinance.common.exception.BusinessException;
import com.microfinance.user.domain.model.DocumentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.file-storage.base-path:/var/microfinance/documents}")
    private String basePath;

    public String storeFile(MultipartFile file, String userId, DocumentType documentType) throws IOException {
        log.info("Storing file for user: {}, type: {}", userId, documentType);

        // Create directory structure: basePath/year/month/userId/documentType/
        LocalDate now = LocalDate.now();
        String year = now.format(DateTimeFormatter.ofPattern("yyyy"));
        String month = now.format(DateTimeFormatter.ofPattern("MM"));
        
        Path directoryPath = Paths.get(basePath, year, month, userId, documentType.toString().toLowerCase());
        
        try {
            Files.createDirectories(directoryPath);
        } catch (IOException e) {
            log.error("Could not create directory: {}", directoryPath, e);
            throw new BusinessException("Failed to create storage directory");
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String filename = documentType.toString().toLowerCase() + "_" + System.currentTimeMillis() + extension;
        
        Path filePath = directoryPath.resolve(filename);

        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Could not store file: {}", filePath, e);
            throw new BusinessException("Failed to store file");
        }

        String relativePath = Paths.get(basePath).relativize(filePath).toString();
        log.info("File stored successfully: {}", relativePath);
        
        return relativePath;
    }

    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(basePath, filePath);
            Files.deleteIfExists(path);
            log.info("File deleted: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath, e);
        }
    }

    public Path getFilePath(String relativePath) {
        return Paths.get(basePath, relativePath);
    }

    public boolean fileExists(String relativePath) {
        return Files.exists(getFilePath(relativePath));
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}