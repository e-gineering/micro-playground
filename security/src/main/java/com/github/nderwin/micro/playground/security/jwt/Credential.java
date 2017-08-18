package com.github.nderwin.micro.playground.security.jwt;

import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

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

    public Date getExpirationDate() {
        return (null == this.claims) ? null : new Date(this.claims.getExpiration().getTime());
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
