package org.dat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dat.service.LocalStorageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class ChatFileController {

    private final LocalStorageService storageService;
    @PostMapping("/upload-file")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = storageService.storeFile(file); // Gọi service để lưu trữ file
            return ResponseEntity.ok(Map.of("url", fileUrl));
        } catch (Exception e) {
            log.error("Lỗi khi tải lên file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Không thể tải lên file."));
        }
    }

    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("image") MultipartFile image) {
        try {
            String imageUrl = storageService.storeImage(image); // Gọi service để lưu trữ ảnh
            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (Exception e) {
            log.error("Lỗi khi tải lên ảnh: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error",
                    "Không thể tải lên ảnh."));
        }
    }

    @GetMapping(
            value = "/get-image/{filename}",
            produces = MediaType.IMAGE_JPEG_VALUE // Hoặc MediaType.IMAGE_PNG_VALUE tùy thuộc vào loại ảnh
    )
    public @ResponseBody byte[] getImage(@PathVariable String filename) throws IOException {
        Path imagePath = Paths.get("./uploads/images/" + filename);
        if (!Files.exists(imagePath)) {
            // Xử lý trường hợp file không tồn tại, ví dụ: trả về một ảnh mặc định hoặc lỗi
            return null; // Hoặc throw new IOException("Không tìm thấy ảnh: " + filename);
        }
        return Files.readAllBytes(imagePath);
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Path filePath = Paths.get("./uploads/files/" + filename);
        Resource resource = new FileSystemResource(filePath);

        if (resource.exists()) {
            try {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                        .filename(filename)
                        .build();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentDisposition(contentDisposition);
                headers.setContentType(MediaType.parseMediaType(contentType));

                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(resource.contentLength())
                        .body((Resource) resource); // Đúng kiểu Resource
            } catch (IOException e) {
                log.error("Lỗi khi tải file xuống: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
