package com.github.nderwin.micro.playground.security;

import com.github.nderwin.micro.playground.jwt.Credential;
import com.github.nderwin.micro.playground.jwt.TokenHandler;
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

    private static final String BEARER = "Bearer ";

    @Inject
    IdentityStoreHandler identityStoreHandler;
    
    @Inject
    TokenHandler tokenHandler;

    @Override
    public AuthenticationStatus validateRequest(final HttpServletRequest request, final HttpServletResponse response, final HttpMessageContext context) throws AuthenticationException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if (null != authHeader && authHeader.startsWith(BEARER)) {
            String token = authHeader.substring(BEARER.length());

            Credential credential = tokenHandler.retrieveCredential(token);
            
            CredentialValidationResult result = identityStoreHandler.validate(credential);
            
            if (VALID == result.getStatus()) {
                return context.notifyContainerAboutLogin(result.getCallerPrincipal(), result.getCallerGroups());
            } else {
                return context.responseUnauthorized();
            }
        }

        return context.doNothing();
    }

}
