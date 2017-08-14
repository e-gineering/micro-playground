package com.github.nderwin.micro.playground.jwt;

import io.jsonwebtoken.Claims;
import java.util.List;

public class Credential implements javax.security.enterprise.credential.Credential {

    private final Claims claims;

    public Credential(final Claims claims) {
        this.claims = claims;
    }

    public String getSubject() {
        return (null == this.claims) ? null : this.claims.getSubject();
    }
    
    public List getScope() {
        return (null == this.claims) ? null : this.claims.get("scope", List.class);
    }
}
