package org.dat.controller;

import org.dat.dto.request.CreateRoleUserRequest;
import org.dat.dto.response.Response;
import org.dat.entity.RoleUser;
import org.dat.service.RoleUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/role-user")
@RequiredArgsConstructor
public class RoleUserController {
    private final RoleUserService roleUserService;

    @PostMapping("/add-role-user/{userId}")
    public Response<RoleUser> createRoleUser(@PathVariable("userId") UUID userId,
                                             @RequestBody CreateRoleUserRequest request) {
        return Response.of(roleUserService.createRoleUserService(userId, request));
    }
}
