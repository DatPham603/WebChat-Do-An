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

//    @MessageMapping("/chat.send")
//    public void sendMessage(@Payload Chat chatMessage,
//                            SimpMessageHeaderAccessor headerAccessor) {
//        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
//        String senderUserId = (String) sessionAttributes.get("userId");
//        String senderUsername = (String) sessionAttributes.get("username");
//        String senderEmail = (String) sessionAttributes.get("email");
//        String receiverId = chatMessage.getReceiverId().toString();
//
//        // Lưu tin nhắn vào database (nếu cần)
//        Chat savedChat = chatRepository.save(chatMessage);
//
//        // Gửi tin nhắn đến queue của người nhận
//        //Tham số thứ 2, destination, là endpoint mà client cần subscribe
//        messagingTemplate.convertAndSendToUser(String.valueOf(receiverId), "/queue/reply", savedChat);
//
//        // Gửi tin nhắn đến queue của người gửi để hiển thị tin nhắn đã gửi
//        messagingTemplate.convertAndSendToUser(String.valueOf(senderUserId), "/queue/reply", savedChat);
//        log.info(senderUserId + "to" + receiverId);
//        // Sử dụng thông tin người gửi nếu cần
//        System.out.println("Message from " + senderUsername + " (" + senderEmail + ")");
//    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload Chat chatMessage, Principal principal) {
        if (principal instanceof UserPrincipal userPrincipal) {
            String senderId = userPrincipal.getUserId();
            String senderUsername = userPrincipal.getUsername();
            chatMessage.setDeleted(false);
            chatRepository.save(chatMessage);

            // ✅ Gán thông tin người gửi vào tin nhắn
            chatMessage.setSenderId(UUID.fromString(senderId)); // bạn cần có field này trong Chat
            chatMessage.setSenderName(senderUsername); // nếu cần

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
    //    @SendTo("/topic/public") //dùng cho topic, ai dky vào topic thì nhận được
    public void sendRoomMessage(@Payload Chat chatMessage,
                                @DestinationVariable String roomId,
                                SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        String senderUserId = (String) sessionAttributes.get("userId");
        String senderUsername = (String) sessionAttributes.get("username");
        String senderEmail = (String) sessionAttributes.get("email");

        // Lưu tin nhắn vào database (nếu cần)
        Chat savedChat = chatRepository.save(chatMessage);

        // Gửi tin nhắn đến topic của room
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId, savedChat);

        // Sử dụng thông tin người gửi nếu cần
        System.out.println("Message from " + senderUsername + " (" + senderEmail + ")");
    }

}

