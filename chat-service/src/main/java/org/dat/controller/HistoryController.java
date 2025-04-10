package org.dat.controller;

import org.dat.dto.dto.ChatDTO;
import org.dat.dto.dto.Response;
import org.dat.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats")
@CrossOrigin(origins = "http://localhost:4200")
public class HistoryController {
    private final ChatService chatService;

    public HistoryController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/history/{userId}/{receiverId}")
    public Response<List<ChatDTO>> getChatHistory(
            @PathVariable("userId") UUID userId,
            @PathVariable("receiverId") UUID receiverId) {
        return Response.of(chatService.getMessagesByReceiver(userId, receiverId));
    }

    @GetMapping("/room/history/{groupId}")
    public Response<List<ChatDTO>> getGroupChatHistory(
            @PathVariable("groupId") UUID groupId) {
        return Response.of(chatService.getGroupHistoryMessage(groupId));
    }
}
