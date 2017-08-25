package com.github.nderwin.micro.playground.security.jwt;

import java.util.HashSet;
import java.util.Set;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.CredentialValidationResult;

import static javax.security.enterprise.identitystore.CredentialValidationResult.NOT_VALIDATED_RESULT;

public class IdentityStore implements javax.security.enterprise.identitystore.IdentityStore {
    
    @Override
    public CredentialValidationResult validate(final Credential credential) {
        if (credential instanceof TokenCredential) {
            return validate((TokenCredential) credential);
        }

        return NOT_VALIDATED_RESULT;
    }

    private CredentialValidationResult validate(final TokenCredential credential) {
        String caller = credential.getSubject();
        Set<String> groups = new HashSet<>(credential.getScope());
        
        return new CredentialValidationResult(caller, groups);
    }
}
