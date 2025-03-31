package org.dat.service;

import org.dat.entity.Role;
import org.dat.entity.RolePermission;
import org.dat.entity.RoleUser;
import org.dat.entity.User;
import org.dat.enums.EnumRole;
import lombok.RequiredArgsConstructor;
import org.dat.repository.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserInforDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleUserRepository roleUserRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Lấy tất cả các vai trò của user
        List<RoleUser> roleUsers = roleUserRepository.findAllByUserId(user.getId());
        if (roleUsers.isEmpty()) {
            throw new RuntimeException("User has no roles assigned");
        }

        // Lấy danh sách role
        List<Role> roles = roleUsers.stream()
                .map(roleUser -> roleRepository.findById(roleUser.getRoleId())
                        .orElseThrow(() -> new RuntimeException("Can't find role with ID: " + roleUser.getRoleId())))
                .toList();

        // Lấy danh sách quyền từ các role
        Set<String> permissions = new HashSet<>();
        boolean isAdmin = false;

        for (Role role : roles) {
            List<String> rolePermissions = rolePermissionRepository.findAllByRoleId(role.getId())
                    .stream()
                    .map(RolePermission::getPermissionId)
                    .map(permissionId -> permissionRepository.findById(permissionId)
                            .map(permission -> permission.getResourceCode() + "_" + permission.getScope())
                            .orElse("Unknown permission"))
                    .toList();

            permissions.addAll(rolePermissions);

            // Nếu có ít nhất 1 role là Admin, đánh dấu user là Admin
            if (Boolean.TRUE.equals(role.getIsAdmin())) {
                isAdmin = true;
            }
        }

        // Chuyển đổi danh sách quyền sang GrantedAuthority
        List<GrantedAuthority> authorities = new ArrayList<>(
                permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList()
        );

        // Nếu user có role admin, thêm quyền ADMIN
        if (isAdmin) {
            authorities.add(new SimpleGrantedAuthority(EnumRole.ADMIN.name()));
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

}
