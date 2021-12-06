package com.epam.esm.gift_system.service.security;

import com.epam.esm.gift_system.repository.model.User;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUserDetailsBuilder {
    private static final boolean ACTIVE = true;

    private SecurityUserDetailsBuilder() {
    }

    public static UserDetails create(User user) {
        return new SecurityUser(
                user.getId(),
                user.getName(),
                user.getPassword(),
                user.getRole().getAuthorities(),
                ACTIVE
        );
    }
}