package org.dat.controller;


import lombok.RequiredArgsConstructor;
import org.dat.dto.response.FriendDTO;
import org.dat.dto.response.Response;
import org.dat.service.FriendService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("api/v1/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;

    @PostMapping("/send-request")
    public Response<Void> sendFriendRequest(@RequestParam UUID userId, @RequestParam UUID friendId) {
        friendService.addFriend(userId, friendId);
        return Response.ok();
    }

    @PostMapping("/accept-request")
    public Response<Void> acceptFriendRequest(@RequestParam UUID userId, @RequestParam UUID friendId) {
        friendService.acceptFriendRequest(userId, friendId);
        return Response.ok();
    }

    @GetMapping("/get-list-friend/{userId}")
    public Response<List<FriendDTO>> getListFriend(@PathVariable UUID userId) {
        return Response.of(friendService.getFriendByUserId(userId));
    }

    @GetMapping("/get-list-friend-by-email/{mail}")
    public Response<List<FriendDTO>> getListFriend(@PathVariable String mail) {
        return Response.of(friendService.getFriendByUserMail(mail));
    }
}
