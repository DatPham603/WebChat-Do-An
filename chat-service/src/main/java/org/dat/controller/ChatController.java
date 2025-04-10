package org.dat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dat.config.UserPrincipal;
import org.dat.entity.Chat;
import org.dat.repository.ChatRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload Chat chatMessage, Principal principal) {
        if (principal instanceof UserPrincipal userPrincipal) {
            String senderId = userPrincipal.getUserId();
            String senderUsername = userPrincipal.getUsername();
            chatMessage.setDeleted(false);
            chatMessage.setSenderId(UUID.fromString(senderId));
            chatMessage.setSenderName(senderUsername);
            chatRepository.save(chatMessage);

            // ✅ Gửi tin nhắn tới người nhận (1-1)
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getReceiverId().toString(),
                    "/queue/messages",
                    chatMessage
            );
            messagingTemplate.convertAndSendToUser(
                    senderId,
                    "/queue/messages", // hoặc "/queue/reply"
                    chatMessage
            );
        }
    }

    @MessageMapping("/chat.send.room/{roomId}")
    public void sendRoomMessage(@Payload Chat chatMessage,
                                @DestinationVariable String roomId,
                                Principal principal) {
        if (principal instanceof UserPrincipal userPrincipal) {
            String senderId = userPrincipal.getUserId();
            String senderUsername = userPrincipal.getUsername();
            chatMessage.setDeleted(false);
            chatMessage.setSenderId(UUID.fromString(senderId));
            chatMessage.setSenderName(senderUsername);
            chatRepository.save(chatMessage);

            System.out.println("Tin nhắn" + chatMessage.getContent() + " được gửi đi " + roomId);
            messagingTemplate.convertAndSend("/topic/rooms/" + roomId, chatMessage);

            System.out.println("Message from " + chatMessage.getContent() + " in room " + roomId);
        }
    }

}

