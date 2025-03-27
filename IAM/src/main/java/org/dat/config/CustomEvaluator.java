package org.dat.config;


import org.dat.entity.User;
import org.dat.enums.EnumRole;
import org.dat.repository.PermissionRepository;
import org.dat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class CustomEvaluator implements PermissionEvaluator {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    public CustomEvaluator() {
    }

    @Override
    public boolean hasPermission(Authentication authentication,
                                 Object targetDomainObject,
                                 Object permission) {
        String searchKeyword;
        searchKeyword = authentication.getName();
        User user = getUserById(searchKeyword);

        if (hasAdminRole(user)) {
            return true;
        }
        return hasPermissionForResource(user, targetDomainObject, permission);
    }

    private User getUserById(String searchKeyword) {
        return userRepository.findByEmail(searchKeyword).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean hasAdminRole(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            return userDetails.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals(EnumRole.ADMIN.name()));
        }
        return false;
    }

    private boolean hasPermissionForResource(User user, Object targetDomainObject, Object permission) {
        return permissionRepository.findPermissionIdByUserAndResourceCodeAndScope(user.getId(),
                        targetDomainObject.toString(),
                        permission.toString())
                .isPresent();
    }

    @Override
    public boolean hasPermission(Authentication authentication,
                                 Serializable targetId,
                                 String targetType, Object permission) {
        return false;
    }

}

