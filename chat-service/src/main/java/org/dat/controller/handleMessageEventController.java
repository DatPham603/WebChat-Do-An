package org.dat.controller;

import lombok.extern.slf4j.Slf4j;
import org.dat.dto.dto.Response;
import org.dat.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
public class handleMessageEventController {
    private final ChatService chatService;

    public handleMessageEventController(ChatService chatService) {
        this.chatService = chatService;
    }

    @DeleteMapping("/delete-message/{senderId}/{messageId}")
    public Response<String> deleteMessage(@PathVariable("senderId") UUID senderId,
            @PathVariable("messageId") UUID messageId) {
        return Response.of(chatService.deleteMessageBySenderId(senderId,messageId));
    }
}
