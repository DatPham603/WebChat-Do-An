package org.dat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalStorageService {
    private final Path storagePath;

    public LocalStorageService(@Value("${storage.path}") String storagePath) {
        this.storagePath = Paths.get(storagePath);
        try {
            Files.createDirectories(this.storagePath);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ", e);
        }
    }

    public String storeFile(byte[] fileData, String fileName) throws IOException {
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        Path filePath = this.storagePath.resolve(uniqueFileName);
        Files.copy(new java.io.ByteArrayInputStream(fileData), filePath, StandardCopyOption.REPLACE_EXISTING);
        return "/storage/" + uniqueFileName;
    }

    public byte[] loadFile(String fileUrl) throws IOException {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        Path filePath = this.storagePath.resolve(fileName);
        return Files.readAllBytes(filePath);
    }

    public String storeImage(byte[] imageData) throws IOException {
        String fileName = UUID.randomUUID().toString() + ".jpg";
        Path filePath = this.storagePath.resolve(fileName);
        Files.copy(new java.io.ByteArrayInputStream(imageData), filePath, StandardCopyOption.REPLACE_EXISTING);
        return "/images/" + fileName;
    }

    public byte[] loadImage(String imageUrl) throws IOException {
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        Path filePath = this.storagePath.resolve(fileName);
        return Files.readAllBytes(filePath);
    }

    public Path getStoragePath() {
        return this.storagePath;
    }
}
