package com.e_gineering.micro.playground.security;

import com.e_gineering.micro.playground.security.control.TokenCredential;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import static javax.security.enterprise.identitystore.CredentialValidationResult.Status.VALID;

@ApplicationScoped
public class AuthenticationMechanism implements HttpAuthenticationMechanism {

    @Inject
    IdentityStoreHandler identityStoreHandler;
    
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
    
    @Override
    public AuthenticationStatus validateRequest(final HttpServletRequest request, final HttpServletResponse response, final HttpMessageContext context) throws AuthenticationException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if (null != authHeader) {
            CredentialValidationResult result = identityStoreHandler.validate(new TokenCredential(authHeader));
            
            if (VALID == result.getStatus()) {
                return context.notifyContainerAboutLogin(result.getCallerPrincipal(), result.getCallerGroups());
            } else {
                return context.responseUnauthorized();
            }
        }

        return context.doNothing();
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

}
