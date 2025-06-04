package org.dat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dat.config.UserPrincipal;
import org.dat.entity.Chat;
import org.dat.repository.ChatRepository;
import org.dat.service.LocalStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
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
    private final LocalStorageService storageService;

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

    @MessageMapping("/webrtc.callended")
    public void handleCallEnded(@Payload Map<String, Object> payload, Principal principal) {
        try {
            String receiverId = (String) payload.get("receiverId");
            String callerId = (String) payload.get("callerId");

            log.info("Handling call ended from {} to {}", callerId, receiverId);

            Map<String, Object> callEndedPayload = Map.of(
                    "callerId", callerId
            );

            messagingTemplate.convertAndSendToUser(
                    receiverId,
                    "/queue/webrtc/callended",
                    callEndedPayload
            );
        } catch (Exception e) {
            log.error("Error handling call ended: ", e);
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

