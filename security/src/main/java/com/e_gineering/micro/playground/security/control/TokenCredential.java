package com.e_gineering.micro.playground.security.control;

import javax.security.enterprise.credential.Credential;

public class TokenCredential implements Credential {

    private final String token;

    public TokenCredential(final String token) {
        if (null == token) {
            throw new IllegalArgumentException("Token must not be null");
        }
        
        this.token = token;
    }

    public String getToken() {
        return token;
    }

}
