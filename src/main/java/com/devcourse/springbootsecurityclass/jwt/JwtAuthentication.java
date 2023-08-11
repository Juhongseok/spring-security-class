package com.devcourse.springbootsecurityclass.jwt;

import org.springframework.util.StringUtils;

public record JwtAuthentication (String token, String username) {

    public JwtAuthentication {
        validate(token);
        validate(username);
    }

    private void validate(String target) {
        if (!StringUtils.hasText(target)) {
            throw new IllegalArgumentException();
        }
    }
}
