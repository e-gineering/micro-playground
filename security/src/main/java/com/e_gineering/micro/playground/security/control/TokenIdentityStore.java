package com.e_gineering.micro.playground.security.control;

import com.e_gineering.micro.playground.security.AuthenticationMechanism;
import com.e_gineering.micro.playground.security.entity.Caller;
import com.e_gineering.micro.playground.security.entity.InvalidToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;

import static javax.security.enterprise.identitystore.CredentialValidationResult.NOT_VALIDATED_RESULT;

public class TokenIdentityStore implements IdentityStore {

    private static final Logger LOG = Logger.getLogger(TokenIdentityStore.class.getName());

    private static final String BEARER = "Bearer ";

    @PersistenceContext
    EntityManager em;

    @Inject
    AuthenticationMechanism authMechanism;

    @Override
    public CredentialValidationResult validate(final Credential credential) {
        if (credential instanceof TokenCredential) {
            return validate((TokenCredential) credential);
        }

        return NOT_VALIDATED_RESULT;
    }

    public String createCredential(final Caller c) {
        Date now = new Date();

        // Expire 1 hour from now
        // TODO - make this configurable?
        Calendar expiration = Calendar.getInstance();
        expiration.setTime(now);
        expiration.add(Calendar.HOUR, 1);

        String jwt = Jwts.builder()
                .setSubject(c.getUsername())
                .claim("scope", c.getRoles())
                .setId(UUID.randomUUID().toString())
                .setExpiration(expiration.getTime())
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.RS256, authMechanism.getPrivateKey())
                .compact();

        return jwt;
    }
    
    public void invalidate(final String authHeader) {
        String token = stripHeader(authHeader);
        Claims claims = parseClaims(token);
        
        InvalidToken it = new InvalidToken(token, claims.getExpiration());
        em.persist(it);
    }

    private CredentialValidationResult validate(final TokenCredential credential) {
        String token = stripHeader(credential.getToken());
        Claims claims = parseClaims(token);

        if (null == claims) {
            return CredentialValidationResult.INVALID_RESULT;
        } else {
            String caller = claims.getSubject();
            Set<String> groups = new HashSet<>(claims.get("scope", List.class));

            return new CredentialValidationResult(caller, groups);
        }
    }

    private Claims parseClaims(final String token) {
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(authMechanism.getPrivateKey())
                    .parseClaimsJws(token);

            if (null != em.find(InvalidToken.class, token)) {
                return null;
            }

            return claims.getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException ex) {
            LOG.log(Level.WARNING, ex.getMessage(), ex);

            return null;
        }

    }

    private String stripHeader(final String authorizationHeader) {
        if (authorizationHeader.startsWith(BEARER)) {
            return authorizationHeader.substring(BEARER.length());
        }

        return authorizationHeader;
    }

}
