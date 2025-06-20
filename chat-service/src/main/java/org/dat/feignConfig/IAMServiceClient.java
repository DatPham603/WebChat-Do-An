package org.dat.feignConfig;


import org.dat.dto.dto.Response;
import org.dat.dto.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "iam", url = "http://localhost:8989/api/v1/users", configuration = FeignClientConfig.class)
public interface IAMServiceClient {
    @GetMapping("/find-by-email/{email}")
    Response<UserDTO> getUserInforbyEmail(@PathVariable("email") String email);

    @GetMapping("/{userId}")
    Response<UserDTO> getUserInforbyUserId(@PathVariable("userId") UUID userId);

    @GetMapping("/get-users-by-ids")
    public Response<List<UserDTO>> getUsersByIds(@RequestParam("userIds") List<UUID> userIds);
}
