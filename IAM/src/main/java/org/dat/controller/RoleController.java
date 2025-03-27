package org.dat.controller;


import lombok.RequiredArgsConstructor;
import org.dat.dto.request.CreateOrUpdateRoleRequest;
import org.dat.dto.response.Response;
import org.dat.dto.response.RoleDTO;
import org.dat.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    @PostMapping("/add-role")
    public Response<RoleDTO> createRole(@RequestBody CreateOrUpdateRoleRequest request) {
        return Response.of(roleService.addRole(request));
    }

    @DeleteMapping("/soft-delete-role/{roleId}")
    public Response<Void> softDeleteRole(@PathVariable("roleId") UUID roleId) {
        roleService.softDelete(roleId);
        return Response.ok();
    }

    @GetMapping("/get-all-role")
    public Response<Page<RoleDTO>> getAllRole(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return Response.of(roleService.getRoles(page, size));
    }

    @GetMapping("/get-role-infor/{roleId}")
    public Response<RoleDTO> getRole(@PathVariable("roleId") UUID roleId) {
        return Response.of(roleService.getRoleById(roleId));
    }
}
