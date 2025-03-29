package org.dat.controller;

import org.dat.dto.request.CreateRolePermissionRequest;
import org.dat.dto.response.Response;
import org.dat.entity.RolePermission;
import org.dat.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/role-permissions")
@RequiredArgsConstructor
public class RolePermissionController {
    private final RolePermissionService rolePermissionService;

    @PostMapping("add-role-permission")
    public Response<RolePermission> createRolePermission(@RequestBody CreateRolePermissionRequest request) {
        return Response.of(rolePermissionService.addRolePermission(request));
    }
}
