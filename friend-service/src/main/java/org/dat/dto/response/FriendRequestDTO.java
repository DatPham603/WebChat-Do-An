package org.dat.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestDTO {
    private UUID receiverId;
    private UUID senderId;
    private String senderName;
    private String senderEmail;
    private String senderAvatar;
    private UUID requestId;
}
