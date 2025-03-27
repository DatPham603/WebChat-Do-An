package org.dat.controller;

import lombok.RequiredArgsConstructor;
import org.dat.dto.request.CreateOrUpdatePermissionRequest;
import org.dat.dto.response.PermissionDTO;
import org.dat.dto.response.Response;
import org.dat.service.PermissionService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping("/add-permission")
    public Response<PermissionDTO> createRole(@RequestBody CreateOrUpdatePermissionRequest request) {
        return Response.of(permissionService.createOrUpdateRole(request));
    }

    @DeleteMapping("/soft-delete-permission/{permissionId}")
    public Response<Void> softDeletePermission(@PathVariable("permissionId") UUID permissionId) {
        permissionService.softDelete(permissionId);
        return Response.ok();
    }

    @GetMapping("/get-all-permission")
    public Response<Page<PermissionDTO>> getAllPermission(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        return Response.of(permissionService.getPermissions(page, size));
    }

    @GetMapping("/get-by-role/{roleId}")
    public Response<List<PermissionDTO>> getPermission(@PathVariable("roleId") UUID roleId) {
        return Response.of(permissionService.getPermissionByRoleId(roleId));
    }

}
