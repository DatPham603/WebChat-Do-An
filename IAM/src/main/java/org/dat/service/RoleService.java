package org.dat.service;

import lombok.AllArgsConstructor;
import org.dat.dto.request.CreateOrUpdateRoleRequest;
import org.dat.dto.response.PermissionDTO;
import org.dat.dto.response.RoleDTO;
import org.dat.entity.Permission;
import org.dat.entity.Role;
import org.dat.entity.RolePermission;
import org.dat.mapper.PermissionMapper;
import org.dat.mapper.RoleMapper;
import org.dat.repository.PermissionRepository;
import org.dat.repository.RolePermissionRepository;
import org.dat.repository.RoleRepository;
import org.dat.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RoleService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    public RoleDTO addRole(CreateOrUpdateRoleRequest request) {
        if (roleRepository.findByCode(request.getCode()).isPresent()) {
            throw new IllegalArgumentException("code existed");
        }
        Role role = roleMapper.fromRoleRequest(request);
        role.setDeleted(false);
        roleRepository.save(role);
        return roleMapper.fromRole(role);
//        RoleDTO.builder()
//                .code(role.getCode())q
//                .description(role.getDescription())
//                .isAdmin(role.isAdmin())
//                .name(role.getName())
//                .build()
    }

    public void softDelete(UUID roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() ->
                new RuntimeException("Not found role"));
        role.setDeleted(true);
        roleRepository.save(role);
    }

    public Page<RoleDTO> getRoles(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return roleRepository.findAll(pageable).map(roleMapper::fromRole);
    }

    public RoleDTO getRoleById(UUID roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() ->
                new RuntimeException("Not found role"));
        RoleDTO roleDTO = roleMapper.fromRole(role);
        List<PermissionDTO> permissions = enrichPermission(roleId);
        roleDTO.setPermissions(permissions);
        return roleDTO;
    }

    private List<PermissionDTO> enrichPermission(UUID roleId) {
        List<RolePermission> rolePermission = rolePermissionRepository.findAllByRoleId(roleId);
        List<UUID> permissionIds = rolePermission.stream().map(RolePermission::getPermissionId).toList();
        List<Permission> permissions = permissionRepository.findAllById(permissionIds);
        return permissionMapper.fromPermissions(permissions);
    }
}
