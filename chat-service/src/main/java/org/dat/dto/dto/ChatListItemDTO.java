package org.dat.dto.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ChatListItemDTO {
    private String type; // "user" hoặc "group"
    private UUID id;
    private String name;
    private String email;
    private String avatar;
    private Integer memberCount;
    private String lastMessage;
    private Instant lastActive;
    // Các trường khác bạn có thể muốn thêm như unreadCount, v.v.
}