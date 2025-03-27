package org.dat.service;


import lombok.AllArgsConstructor;
import org.dat.dto.request.CreateRoleUserRequest;
import org.dat.entity.Role;
import org.dat.entity.RoleUser;
import org.dat.entity.User;
import org.dat.repository.RoleRepository;
import org.dat.repository.RoleUserRepository;
import org.dat.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class RoleUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleUserRepository roleUserRepository;

    public RoleUser createRoleUserService(UUID userId, CreateRoleUserRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));
        Role role = roleRepository.findById(request.getRoleId()).orElseThrow(() ->
                new RuntimeException("Role not found"));
        boolean check = roleUserRepository.findAllByUserId(userId).stream()
                .anyMatch(roleUser -> roleUser.getRoleId().equals(role.getId()));
        if (check) {
            throw new RuntimeException("Role user existed");
        }
        return roleUserRepository.save(RoleUser.builder().
                roleId(role.getId())
                .userId(user.getId())
                .deleted(false)
                .build());
    }
}
