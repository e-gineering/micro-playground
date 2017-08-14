package com.github.nderwin.micro.playground.security;

import com.github.nderwin.micro.playground.security.control.BCryptPasswordHash;
import java.util.Base64;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.security.enterprise.AuthenticationException;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import javax.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import javax.security.enterprise.credential.Password;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import javax.security.enterprise.identitystore.IdentityStoreHandler;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import static javax.security.enterprise.identitystore.CredentialValidationResult.Status.VALID;

@DatabaseIdentityStoreDefinition(
    dataSourceLookup = "${'java:/shareDS'}",
    callerQuery = "#{'select password from security.caller where username = ?'}",
    groupsQuery = "select 'USER' from security.caller where username = ?",
    hashAlgorithm = BCryptPasswordHash.class
)
@ApplicationScoped
public class AuthenticationMechanism implements HttpAuthenticationMechanism {

    private static final String BEARER = "Bearer ";

    @Inject
    IdentityStoreHandler identityStoreHandler;

    @Inject
    private Pbkdf2PasswordHash passwordHash;
    
    @Override
    public AuthenticationStatus validateRequest(final HttpServletRequest request, final HttpServletResponse response, final HttpMessageContext context) throws AuthenticationException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if (null != authHeader && authHeader.startsWith(BEARER)) {
            String token = authHeader.substring(BEARER.length());

            // TODO - parse and validate a JWT
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");
            
            String username = parts[0];
            Password password = new Password(parts[1]);
            
            CredentialValidationResult result = identityStoreHandler.validate(new UsernamePasswordCredential(username, password));
            
            if (VALID == result.getStatus()) {
                return context.notifyContainerAboutLogin(result.getCallerPrincipal(), result.getCallerGroups());
            } else {
                return context.responseUnauthorized();
            }
        }

        return context.doNothing();
    }

}
