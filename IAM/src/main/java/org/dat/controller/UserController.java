package org.dat.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.dat.dto.request.LoginRequest;
import org.dat.dto.request.RefreshTokenRequest;
import org.dat.dto.request.RegisterRequest;
import org.dat.dto.request.UpdateUserInforRequest;
import org.dat.dto.response.JwtDTO;
import org.dat.dto.response.Response;
import org.dat.dto.response.UserAuthDTO;
import org.dat.dto.response.UserDTO;
import org.dat.exception.UserExistedException;
import org.dat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public Response<UserDTO> register(@RequestBody RegisterRequest request) throws UserExistedException {
        return Response.of(userService.register(request));
    }

    @PostMapping("/login")
    public Response<JwtDTO> login(@RequestBody LoginRequest request) {
        return Response.of(userService.login(request));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasPermission('Document','DELETE')")
    public Response<UserDTO> getUserById(@PathVariable("userId") UUID userId) {
        return Response.of(userService.getUserInfor(userId));
    }

    @PostMapping("/refresh-token")
    public Response<String> getRefreshToken(@RequestParam("refresh_token") RefreshTokenRequest request) {
        return Response.of(userService.refreshToken(request));
    }

    @GetMapping("/get-all-users")
    public Response<Page<UserDTO>> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return Response.of(userService.getAllUsers(page,size));
    }

    @PutMapping("/update-users-infor/{userId}")
    public Response<UserDTO> updateUserInfor(@PathVariable("userId") UUID userId,
                                             @RequestBody UpdateUserInforRequest request) {
        return Response.of(userService.updateUserInfor(userId, request));
    }

    @PostMapping("/logout-account")
    public ResponseEntity<?> logout(@RequestHeader("authorization") String authorizationHeader,
                                    @RequestParam("refresh_token") String refreshToken) {
        return ResponseEntity.ok(userService.logout(authorizationHeader, refreshToken));
    }

    @DeleteMapping("/soft-delete-user/{userId}")
    public Response<Void> softDeleteUser(@PathVariable("userId") UUID userId) {
        this.userService.softDeleteUser(userId);
        return Response.ok();
    }

    @GetMapping("/validate")
    public ResponseEntity<UserAuthDTO> validateToken(@RequestParam String token) {
        try {
            UserAuthDTO userInfo = userService.validateToken(token);
            return ResponseEntity.ok(userInfo);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("X-Token-Error", "expired")
                    .build();
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .header("X-Token-Error", "invalid")
                    .build();
        }
    }

}
