package org.dat.service;

import org.dat.entity.Chat;
import org.dat.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public Chat saveMessage(Chat chat) {
        return chatRepository.save(chat);
    }

//    public List<Chat> getMessagesByReceiver(UUID receiverId) {
//        return chatRepository.findByReceiverId(receiverId);
//    }
}
