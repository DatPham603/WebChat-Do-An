package org.dat.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dat.enums.MessageType;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatDTO {
    private UUID id;
    private MessageType type;
    private String content;
    private UUID senderId;
    private String senderName;
    private UUID receiverId;
    private Boolean deleted;
}
