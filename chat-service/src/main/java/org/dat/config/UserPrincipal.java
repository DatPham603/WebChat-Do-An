package org.dat.config;

import java.security.Principal;


public class UserPrincipal implements Principal {
    private final String userId;
    private final String email;
    private final String username;

    public UserPrincipal(String userId, String email, String username) {
        this.userId = userId;
        this.email = email;
        this.username = username;
    }

    @Override
    public String getName() {
        return userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

}
