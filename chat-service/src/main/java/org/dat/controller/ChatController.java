package org.dat.controller;

import lombok.RequiredArgsConstructor;
import org.dat.entity.Chat;
import org.dat.repository.ChatRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatRepository chatRepository;

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public Chat sendMessage(Chat chatMessage) {
        // Lưu tin nhắn vào database (nếu cần)
        Chat savedChat = chatRepository.save(chatMessage);
        return savedChat;
    }
}

