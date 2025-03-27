package org.dat.service;

import lombok.extern.slf4j.Slf4j;
import org.dat.config.JwtTokenUtils;
import org.dat.dto.request.LoginRequest;
import org.dat.dto.request.RefreshTokenRequest;
import org.dat.dto.request.RegisterRequest;
import org.dat.dto.request.UpdateUserInforRequest;
import org.dat.dto.response.JwtDTO;
import org.dat.dto.response.UserDTO;
import org.dat.entity.*;
import org.dat.enums.EnumRole;
import org.dat.exception.UserExistedException;
import org.dat.mapper.UserMapper;
import org.dat.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleUserRepository roleUserRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenUtils jwtTokenUtils;
    private final InvalidTokenRepository invalidTokenRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       RoleUserRepository roleUserRepository,
                       RolePermissionRepository rolePermissionRepository,
                       PermissionRepository permissionRepository,
                       PasswordEncoder passwordEncoder,
                       RefreshTokenService refreshTokenService,
                       JwtTokenUtils jwtTokenUtils,
                       InvalidTokenRepository invalidTokenRepository,
                       UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.roleUserRepository = roleUserRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.jwtTokenUtils = jwtTokenUtils;
        this.invalidTokenRepository = invalidTokenRepository;
        this.userMapper = userMapper;
    }

    public UserDTO register(RegisterRequest registerRequest) throws UserExistedException {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserExistedException("Email already exists");
        }
        String roleCode = (registerRequest.getRoleCode() != null)
                ? registerRequest.getRoleCode().toUpperCase()
                : EnumRole.USER.name();
        Role role = roleRepository.findByCode(roleCode).orElseThrow(()
                -> new RuntimeException("role not found"));
        if (role == null) {
            throw new RuntimeException("Role not found");
        }
        User user = userRepository.save(User.builder()
                .userName(registerRequest.getUserName())
                .address(registerRequest.getAddress())
                .passWord(passwordEncoder.encode(registerRequest.getPassWord()))
                .email(registerRequest.getEmail())
                .deleted(false)
                .dateOfBirth(registerRequest.getDateOfBirth())
                .phoneNumber(registerRequest.getPhoneNumber())
                .build());

        roleUserRepository.save(RoleUser.builder()
                .roleId(role.getId())
                .userId(user.getId())
                .build());

        return UserDTO.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .roleName(enrichRole(user.getId()))
                .perDescription(enrichPermission(role.getId()))
                .build();
    }

    public JwtDTO login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid information"));
        if (!passwordEncoder.matches(request.getPassWord(), user.getPassword())) {
            throw new RuntimeException("invalid information");
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(user,
                null, user.getAuthorities());
        SecurityContext securityContextHolder = SecurityContextHolder.getContext();
        securityContextHolder.setAuthentication(authentication);
        refreshTokenService.deleteByUserId(user.getId());
        String token = jwtTokenUtils.generateToken(user);
        return JwtDTO.builder()
                .accessToken(token)
                .expirationTime(jwtTokenUtils.getExpirationTimeFromToken(token))
                .refreshToken(refreshTokenService.
                        createRefreshToken(user.getId(),
                                UUID.fromString(jwtTokenUtils.getJtiFromToken(token)),
                                jwtTokenUtils.getExpirationTimeFromToken(token))
                        .getRefreshToken())
                .build();
    }

    public String refreshToken(RefreshTokenRequest request) {
        Optional<RefreshToken> refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        if (refreshToken.isPresent()) {
            invalidTokenRepository.save(
                    InvalidToken.builder()
                            .id(refreshToken.get().getAccessTokenId())
                            .expiryTime(refreshToken.get().getAccessTokenExp())
                            .refreshTokenId(refreshToken.get().getId())
                            .build());
            RefreshToken validRefreshToken = refreshTokenService.verifyRefreshToken(refreshToken.get());
            User user = userRepository.findById(validRefreshToken.getUserId()).orElseThrow(() ->
                    new RuntimeException("User not found"));
            return jwtTokenUtils.generateToken(user);
        }
        throw new RuntimeException("refreshToken not found");
    }

    public UserDTO getUserInfor(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));

        List<RoleUser> roleUsers = roleUserRepository.findAllByUserId(user.getId());
        if (roleUsers.isEmpty()) {
            throw new RuntimeException("User has no roles assigned");
        }
        List<Role> roles = roleUsers.stream()
                .map(roleUser -> roleRepository.findById(roleUser.getRoleId())
                        .orElseThrow(() -> new RuntimeException("Can't find role")))
                .toList();
        List<String> roleNames = roles.stream()
                .map(Role::getCode)
                .toList();

        List<String> permissionDescriptions = enrichPermissions(
                roles.stream().map(Role::getId).toList()
        );

        return UserDTO.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .roleName(roleNames)
                .perDescription(permissionDescriptions)
                .build();
    }


    public UserDTO updateUserInfor(UUID userId, UpdateUserInforRequest updateUserInforRequest) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (updateUserInforRequest.getUserName() != null) {
            existingUser.setUserName(updateUserInforRequest.getUserName());
        }
        if (updateUserInforRequest.getPassWord() != null) {
            existingUser.setPassWord(passwordEncoder.encode(updateUserInforRequest.getPassWord()));
        }
        if (updateUserInforRequest.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updateUserInforRequest.getPhoneNumber());
        }
        if (updateUserInforRequest.getAddress() != null) {
            existingUser.setAddress(updateUserInforRequest.getAddress());
        }
        if (updateUserInforRequest.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updateUserInforRequest.getDateOfBirth());
        }
        userRepository.save(existingUser);
        return UserDTO.builder()
                .userName(existingUser.getUsername())
                .email(existingUser.getEmail())
                .address(existingUser.getAddress())
                .dateOfBirth(existingUser.getDateOfBirth())
                .build();
    }

    public void softDeleteUser(UUID userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        existingUser.setDeleted(true);
        userRepository.save(existingUser);
    }

    public Page<UserDTO> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable).map(user -> UserDTO.builder()
                .userName(user.getUsername())
                .email(user.getEmail())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .build());
    }

    public String logout(String accessToken, String refreshToken) {
        if (accessToken.startsWith("Bearer")) {
            accessToken = accessToken.substring(7).trim();
        }
        InvalidToken invalidToken = InvalidToken.builder()
                .id( UUID.fromString(jwtTokenUtils.getJtiFromToken(accessToken)))
                .expiryTime(jwtTokenUtils.getExpirationTimeFromToken(accessToken))
                .refreshTokenId(UUID.fromString(jwtTokenUtils.getJtiFromToken(refreshToken)))
                .build();
        invalidTokenRepository.save(invalidToken);
        return "logout success";
    }

    private List<String> enrichPermissions(List<UUID> roleIds) {
        return roleIds.stream()
                .flatMap(roleId -> rolePermissionRepository.findAllByRoleId(roleId).stream()
                        .map(RolePermission::getPermissionId)
                        .map(permissionId -> permissionRepository.findById(permissionId)
                                .map(permission -> permission.getResourceCode() + "_" + permission.getScope())
                                .orElse("Unknown_Permission")))
                .distinct()
                .toList();
    }


    private List<String> enrichPermission(UUID roleId) {
        return rolePermissionRepository
                .findAllByRoleId(roleId).stream()
                .map(RolePermission::getPermissionId)
                .map(permissionId -> permissionRepository
                        .findById(permissionId)
                        .map(Permission::getScope)
                        .orElse("Unknown Permission"))
                .toList();
    }

    private List<String> enrichRole(UUID userId){
        return roleUserRepository.findAllByUserId(userId).stream()
                .map(RoleUser::getRoleId)
                .map(roleId ->
                        roleRepository.findById(roleId).map(Role::getCode)
                        .orElse("Unknow role")).toList();
    }

}
