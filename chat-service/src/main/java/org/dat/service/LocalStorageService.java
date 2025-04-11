package org.dat.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalStorageService {
    private final Path fileStorageLocation;
    private final Path imageStorageLocation;

    public LocalStorageService() {
        this.fileStorageLocation = Paths.get("./uploads/files").toAbsolutePath().normalize();
        this.imageStorageLocation = Paths.get("./uploads/images").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            Files.createDirectories(this.imageStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ.", e);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation);
        return "/uploads/files/" + fileName; // Trả về đường dẫn tương đối
    }

    public String storeImage(MultipartFile image) throws IOException {
        String imageName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        Path targetLocation = this.imageStorageLocation.resolve(imageName);
        Files.copy(image.getInputStream(), targetLocation);
        return "/uploads/images/" + imageName; // Trả về đường dẫn tương đối
    }
}
