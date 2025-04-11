package org.dat.controller;

import lombok.RequiredArgsConstructor;
import org.dat.dto.dto.ChatListItemDTO;
import org.dat.dto.dto.Response;
import org.dat.service.ChatListService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ChatListController {

    private final ChatListService chatListService;

    @GetMapping("/list")
    public Response<List<ChatListItemDTO>> getChatList(@RequestParam("userId") String userIdStr) {
        UUID currentUserId = UUID.fromString(userIdStr);
        List<ChatListItemDTO> chatList = chatListService.getChatList(currentUserId);
        return Response.of(chatList);
    }
}
