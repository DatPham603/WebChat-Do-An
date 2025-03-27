package org.dat.service;


import lombok.RequiredArgsConstructor;
import org.dat.dto.request.CreateOrUpdatePermissionRequest;
import org.dat.dto.response.PermissionDTO;
import org.dat.entity.Permission;
import org.dat.entity.RolePermission;
import org.dat.mapper.PermissionMapper;
import org.dat.repository.PermissionRepository;
import org.dat.repository.RolePermissionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final RolePermissionRepository rolePermissionRepository;


    public PermissionDTO createOrUpdateRole(CreateOrUpdatePermissionRequest request) {
        if (permissionRepository.existsByScopeAndResourceCode(request.getScope(),
                request.getResourceCode())) {
            throw new IllegalArgumentException("data existed");
        }
        Permission permission = permissionMapper.fromRequest(request);
        permission.setDeleted(false);
        permissionRepository.save(permission);
        return permissionMapper.fromPermission(permission);
    }

    public void softDelete(UUID permissionId) {
        Permission permission = permissionRepository.findById(permissionId).orElseThrow(() ->
                new RuntimeException("Not found permission"));
        permission.setDeleted(true);
        permissionRepository.save(permission);
    }

    public Page<PermissionDTO> getPermissions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return permissionRepository.findAll(pageable).map(permissionMapper::fromPermission);
    }

    public List<PermissionDTO> getPermissionByRoleId(UUID roleId) {
        List<RolePermission> rolePermission = rolePermissionRepository.findAllByRoleId(roleId);
        List<UUID> permissionIds = rolePermission.stream().map(RolePermission::getPermissionId).toList();
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        return permissionMapper.fromPermissions(permissions);
    }

}
