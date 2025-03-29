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
import java.util.List;

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
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        RoleUser roleUser = roleUserRepository.findByUserId(user.getId());
        Role role = roleRepository.findById(roleUser.getRoleId())
                .orElseThrow(() -> new UsernameNotFoundException("role not found"));

        String roleName = roleRepository.findById(roleUser.getRoleId()).map(Role::getCode)
                .orElseThrow(() -> new RuntimeException("role not found"));

        List<String> permissions = rolePermissionRepository.findAllByRoleId(roleUser.getRoleId())
                .stream()
                .map(RolePermission::getPermissionId)
                .map(permissionId -> permissionRepository.findById(permissionId)
                        .map(permission ->
                                        permission.getResourceCode() + "_" + permission.getScope())
                        .orElse("Unknow permission"))
                .toList();

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }
        if (role.getIsAdmin()) {
            authorities.add(new SimpleGrantedAuthority(EnumRole.ADMIN.name()));
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
