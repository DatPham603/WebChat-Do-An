package org.dat.feignConfig;

import org.dat.dto.dto.FriendDTO;
import org.dat.dto.dto.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "friend-service", url = "http://localhost:8010/api/v1/friends", configuration = FeignClientConfig.class)
public interface FriendServiceClient {
    @GetMapping("/get-list-friend-by-email/{mail}")
    Response<List<FriendDTO>> getListFriend(@PathVariable String mail);

}
