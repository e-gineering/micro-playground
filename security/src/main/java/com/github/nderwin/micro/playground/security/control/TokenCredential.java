package com.github.nderwin.micro.playground.security.control;

import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.security.enterprise.credential.Credential;

public class TokenCredential implements Credential {

    private final Claims claims;

    public TokenCredential(final Claims claims) {
        if (null == claims) {
            throw new IllegalArgumentException("Claims must not be null");
        }
        
        this.claims = claims;
    }

    public String getSubject() {
        return this.claims.getSubject();
    }

    public List getScope() {
        return this.claims.get("scope", List.class);
    }

    public Date getExpirationDate() {
        if (null != this.claims.getExpiration()) {
            return new Date(this.claims.getExpiration().getTime());
        }
        
        return null;
    }

    public JsonObject toJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("subject", getSubject());

        JsonArrayBuilder jab = Json.createArrayBuilder();
        getScope().forEach((o) -> {
            jab.add(o.toString());
        });
        job.add("scope", jab.build());
        
        return job.build();
    }
}
