package com.library.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    ADMIN,
    AUTHOR;
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
