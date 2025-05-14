package org.dat.feignConfig;


import org.dat.dto.request.UpdateEmailRequest;
import org.dat.dto.request.UpdateNameRequest;
import org.dat.dto.response.FriendDTO;
import org.dat.dto.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@FeignClient(name = "friend-service", url = "http://localhost:8010/api/v1/friends", configuration = FeignClientConfig.class)
public interface FriendServiceClient {
    @PutMapping("/users/{userId}/name")
    Response<Void> updateFriendName(@PathVariable UUID userId, @RequestBody UpdateNameRequest request);

    @PutMapping("/users/{userId}/email")
    Response<Void> updateFriendEmail(@PathVariable UUID userId, @RequestBody UpdateEmailRequest request);

    @GetMapping("/check")
    Response<Boolean> areFriends(@RequestParam("userId") UUID userId, @RequestParam("friendId") UUID friendId);

    @GetMapping("/users/{userId}/findFriends")
    Response<List<FriendDTO>> findFriends(@PathVariable UUID userId);
}
