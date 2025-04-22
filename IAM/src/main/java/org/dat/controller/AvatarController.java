package org.dat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dat.entity.User;
import org.dat.repository.UserRepository;
import org.dat.service.AvatarStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users/avatar")
@RequiredArgsConstructor
@Slf4j
public class AvatarController {
    private final AvatarStorageService avatarStorageService;
    private final UserRepository userRepository;

    @PostMapping("/upload-avatar")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("avatar") MultipartFile avatar) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            String imageUrl = avatarStorageService.storeAvatar(avatar);
            User user = userRepository.findByEmail(username).get();
            user.setAvatar(imageUrl);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (Exception e) {
            log.error("Lỗi khi tải avatar: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error",
                    "Không thể tải lên ảnh đại diện."));
        }
    }

    @GetMapping(value = "/get-avatar/{filename}",
                produces = MediaType.IMAGE_JPEG_VALUE) // Hoặc MediaType.IMAGE_PNG_VALUE tùy thuộc vào loại ảnh
    public @ResponseBody byte[] getImage(@PathVariable String filename) throws IOException {
        Path imagePath = Paths.get("./uploads/avatar/" + filename);
        if (!Files.exists(imagePath)) {
            // Xử lý trường hợp file không tồn tại, ví dụ: trả về một ảnh mặc định hoặc lỗi
             throw new IOException("Không tìm thấy ảnh đại diện: " + filename);
        }
        return Files.readAllBytes(imagePath);
    }

//    @GetMapping("/test")
//    public ResponseEntity<?> test() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        return ResponseEntity.ok(auth.getName());
//    }
}
