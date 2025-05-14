package org.dat.controller;


import lombok.RequiredArgsConstructor;
import org.dat.dto.request.UpdateEmailRequest;
import org.dat.dto.request.UpdateNameRequest;
import org.dat.dto.response.FriendDTO;
import org.dat.dto.response.Response;
import org.dat.service.FriendService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:4200")
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

    @PutMapping("/users/{userId}/name")
    public Response<Void> updateFriendName(@PathVariable UUID userId, @RequestBody UpdateNameRequest request) {
            friendService.updateFriendNames(userId, request.getUserName());
            return Response.ok();
    }

    @PutMapping("/users/{userId}/email")
    public Response<Void> updateFriendEmail(@PathVariable UUID userId, @RequestBody UpdateEmailRequest request) {
            friendService.updateFriendEmails(userId, request.getEmail());
            return Response.ok();
        }

    @GetMapping("/check")
    public Response<Boolean> areFriends(
            @RequestParam("userId") UUID userId,
            @RequestParam("friendId") UUID friendId) {
        boolean areFriends = friendService.areFriends(userId, friendId);
        return Response.of(areFriends);
    }

    @GetMapping("/users/{userId}/findFriends")
    public Response<List<FriendDTO>> findFriends(@PathVariable UUID userId) {
        return Response.of(friendService.searchAllUserFriends(userId));
    }
}
