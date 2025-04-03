package org.dat.config.constant;

public class SecurityConstants {
    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/users/login/**",
            "/api/v1/users/register/**",
            "/api/v1/users/resetPasswordToken/**",
            "/api/v1/users/logout-account/**",
            "/api/v1/users/confirm-register-email/**",
            "/api/v1/users/confirm-login-email",
            "/api/v1/users/uploads/**",
            "/api/v1/users/refresh-token/**",
            "/api/v1/users/logout/**",
            "/api/v1/users/validate/**",
            "/ws-chat/**"
    };
}
