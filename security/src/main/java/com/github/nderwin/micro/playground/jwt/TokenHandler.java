package com.github.nderwin.micro.playground.jwt;

import com.github.nderwin.micro.playground.security.entity.Caller;
import com.github.nderwin.micro.playground.security.entity.InvalidToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class TokenHandler {

    private static final Logger LOG = Logger.getLogger(TokenHandler.class.getName());
    
    private static final String BEARER = "Bearer ";

    @PersistenceContext
    EntityManager em;
    
    private RSAPrivateKey privateKey;
    
    @PostConstruct
    public void init() {
        KeyPairGenerator keyGenerator;
        
        try {
            keyGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }

        keyGenerator.initialize(2048);
        
        KeyPair kp = keyGenerator.generateKeyPair();
        
        privateKey = (RSAPrivateKey) kp.getPrivate();
    }
    
    public String stripHeader(final String authorizationHeader) {
        if (authorizationHeader.startsWith(BEARER)) {
            return authorizationHeader.substring(BEARER.length());
        }
        
        return authorizationHeader;
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
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
        
        return jwt;
    }
    
    public Credential retrieveCredential(final String token) {
        Credential credential = null;
        
        String jwt = stripHeader(token);
        
        try {
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(privateKey)
                    .parseClaimsJws(jwt);
            
            if (null != em.find(InvalidToken.class, jwt)) {
                throw new ExpiredJwtException(claims.getHeader(), claims.getBody(), "Token has been invalidated");
            }
            
            credential = new Credential(claims.getBody());
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException ex) {
            LOG.log(Level.WARNING, ex.getMessage(), ex);
        }
        
        return credential;
    }
}
