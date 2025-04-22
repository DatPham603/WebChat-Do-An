package org.dat.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class AvatarStorageService {
    private final Path avatarStorageLocation;

    public AvatarStorageService() {
        this.avatarStorageLocation = Paths.get("./uploads/avatar").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.avatarStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục lưu trữ avatar.", e);
        }
    }

    public String storeAvatar(MultipartFile image) throws IOException {
        String imageName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        Path targetLocation = this.avatarStorageLocation.resolve(imageName);
        Files.copy(image.getInputStream(), targetLocation);
        return "/uploads/avatar/" + imageName; // Trả về đường dẫn tương đối đến avatar
    }

    public void deleteAvatar(String avatarUrl) throws IOException {
        try {
            Path filePath = Paths.get(".").resolve(avatarUrl).normalize();
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            } else {
                System.out.println("Không tìm thấy avatar để xóa: " + filePath);
            }
        } catch (IOException e) {
            throw new IOException("Không thể xóa avatar: " + avatarUrl, e);
        }
    }

}
