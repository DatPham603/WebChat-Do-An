package org.dat.service;

import org.dat.dto.request.CreateRolePermissionRequest;
import org.dat.entity.Permission;
import org.dat.entity.Role;
import org.dat.entity.RolePermission;
import org.dat.repository.PermissionRepository;
import org.dat.repository.RolePermissionRepository;
import org.dat.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RolePermissionService {
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public RolePermission addRolePermission(CreateRolePermissionRequest request) {
        Role role = roleRepository.findByCode(request.getCode())
                .orElseThrow(() -> new RuntimeException("Not found role"));
        Permission permission = permissionRepository.findByResourceCodeAndScope(request.getResourceCode(),
                                                                                request.getScope())
                .orElseThrow(() -> new RuntimeException("Not found permission"));
        boolean check = rolePermissionRepository.findAllByRoleId(role.getId()).stream()
                .anyMatch(rolePermission -> rolePermission.getPermissionId().equals(permission.getId()));
        if (check) {
            throw new RuntimeException("Role permission existed");
        }
        return rolePermissionRepository.save(RolePermission.builder()
                .permissionId(permission.getId())
                .roleId(role.getId())
                .deleted(false)
                .build());
    }
}
