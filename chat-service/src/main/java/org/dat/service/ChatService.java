package org.dat.service;

import org.dat.dto.dto.ChatDTO;
import org.dat.entity.Chat;
import org.dat.enums.ContentType;
import org.dat.enums.MessageType;
import org.dat.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public Chat saveMessage(Chat chat) {
        return chatRepository.save(chat);
    }

    public List<ChatDTO> getMessagesByReceiver(UUID userId, UUID receiverId) {
        List<Chat> chatHistorys = chatRepository.findChatByReceiverId(userId, receiverId);
        return chatHistorys.stream().map(chat -> ChatDTO.builder()
                .id(chat.getId())
                .type(chat.getType())
                .senderId(chat.getSenderId())
                .receiverId(chat.getReceiverId())
                .senderName(chat.getSenderName())
                .content(chat.getContent())
                .fileUrl(chat.getFileUrl())
                .contentType(chat.getContentType())
                .deleted(chat.getDeleted())
                .build()).collect(Collectors.toList());
    }

    public List<ChatDTO> getGroupHistoryMessage(UUID groupId) {
        List<Chat> chatHistorys = chatRepository.findByReceiverIdAndType(groupId, MessageType.GROUP_CHAT);
        return chatHistorys.stream().map(chat -> ChatDTO.builder()
                .id(chat.getId())
                .type(chat.getType())
                .senderId(chat.getSenderId())
                .receiverId(chat.getReceiverId())
                .senderName(chat.getSenderName())
                .content(chat.getContent())
                .fileUrl(chat.getFileUrl())
                .contentType(chat.getContentType())
                .deleted(chat.getDeleted())
                .build()).collect(Collectors.toList());
    }

    public List<ChatDTO> getImageChatHistory(UUID userId, UUID receiverId) {
        List<Chat> chatHistorys = chatRepository.findImageChatByReceiverId(userId, receiverId, ContentType.IMAGE);
        return chatHistorys.stream().map(chat -> ChatDTO.builder()
                .id(chat.getId())
                .type(chat.getType())
                .senderId(chat.getSenderId())
                .receiverId(chat.getReceiverId())
                .senderName(chat.getSenderName())
                .content(chat.getContent())
                .fileUrl(chat.getFileUrl())
                .contentType(chat.getContentType())
                .deleted(chat.getDeleted())
                .build()).collect(Collectors.toList());
    }

    public List<ChatDTO> getFileChatHistory(UUID userId, UUID receiverId) {
        List<Chat> chatHistorys = chatRepository.findImageChatByReceiverId(userId, receiverId, ContentType.FILE);
        return chatHistorys.stream().map(chat -> ChatDTO.builder()
                .id(chat.getId())
                .type(chat.getType())
                .senderId(chat.getSenderId())
                .receiverId(chat.getReceiverId())
                .senderName(chat.getSenderName())
                .content(chat.getContent())
                .fileUrl(chat.getFileUrl())
                .contentType(chat.getContentType())
                .deleted(chat.getDeleted())
                .build()).collect(Collectors.toList());
    }

    public List<ChatDTO> getImageGroupHistoryMessage(UUID groupId) {
        List<Chat> chatHistorys = chatRepository.findImageHistoryChatByGroupId(groupId, MessageType.GROUP_CHAT, ContentType.IMAGE);
        return chatHistorys.stream().map(chat -> ChatDTO.builder()
                .id(chat.getId())
                .type(chat.getType())
                .senderId(chat.getSenderId())
                .receiverId(chat.getReceiverId())
                .senderName(chat.getSenderName())
                .content(chat.getContent())
                .fileUrl(chat.getFileUrl())
                .contentType(chat.getContentType())
                .deleted(chat.getDeleted())
                .build()).collect(Collectors.toList());
    }

    public List<ChatDTO> getFileGroupHistoryMessage(UUID groupId) {
        List<Chat> chatHistorys = chatRepository.findImageHistoryChatByGroupId(groupId, MessageType.GROUP_CHAT, ContentType.FILE);
        return chatHistorys.stream().map(chat -> ChatDTO.builder()
                .id(chat.getId())
                .type(chat.getType())
                .senderId(chat.getSenderId())
                .receiverId(chat.getReceiverId())
                .senderName(chat.getSenderName())
                .content(chat.getContent())
                .fileUrl(chat.getFileUrl())
                .contentType(chat.getContentType())
                .deleted(chat.getDeleted())
                .build()).collect(Collectors.toList());
    }


}
