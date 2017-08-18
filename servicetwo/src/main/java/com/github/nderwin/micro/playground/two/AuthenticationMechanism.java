package com.github.nderwin.micro.playground.two;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.credential.CallerOnlyCredential;
import javax.security.enterprise.credential.Credential;
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

    @Override
    public AuthenticationStatus validateRequest(final HttpServletRequest request, final HttpServletResponse response, final HttpMessageContext httpMessageContext) throws AuthenticationException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (null != authHeader) {
            Credential credential = new CallerOnlyCredential(authHeader);
            
            CredentialValidationResult result = identityStoreHandler.validate(credential);
            
            if (VALID == result.getStatus()) {
                return httpMessageContext.notifyContainerAboutLogin(result.getCallerPrincipal(), result.getCallerGroups());
            } else {
                return httpMessageContext.responseUnauthorized();
            }
        }

        return httpMessageContext.doNothing();
    }
    
}
