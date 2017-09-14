package com.e_gineering.micro.playground.two;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.security.enterprise.credential.CallerOnlyCredential;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class IdentityStore implements javax.security.enterprise.identitystore.IdentityStore {

    private static final Logger LOG = Logger.getLogger(IdentityStore.class.getName());
    
    @Override
    public CredentialValidationResult validate(final Credential credential) {
        if (credential instanceof CallerOnlyCredential) {
           return validate((CallerOnlyCredential) credential);
        }
        
        return CredentialValidationResult.NOT_VALIDATED_RESULT;
    }
    
    private CredentialValidationResult validate(final CallerOnlyCredential credential) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://security:8080/security/resources/authentication");
        
        try {
            JsonObject response = target
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, credential.getCaller())
                    .get(JsonObject.class);

            List<JsonString> values = response.getJsonArray("scope").getValuesAs(JsonString.class);
            Set<String> groups = new HashSet<>();
            values.forEach((js) -> {
                groups.add(js.getString());
            });
            
            return new CredentialValidationResult(response.getString("subject"), groups);
        } catch (ProcessingException | WebApplicationException ex) {
            LOG.log(Level.WARNING, ex.getMessage(), ex);
            return CredentialValidationResult.INVALID_RESULT;
        }
    }
}
