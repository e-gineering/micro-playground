package com.github.nderwin.micro.playground.security.control;

import javax.enterprise.context.Dependent;
import javax.security.enterprise.identitystore.PasswordHash;
import org.mindrot.BCrypt;

@Dependent
public class BCryptPasswordHash implements PasswordHash {

    private final String salt = BCrypt.gensalt();
    
    @Override
    public String generate(final char[] password) {
        return BCrypt.hashpw(new String(password), salt);
    }

    @Override
    public boolean verify(final char[] password, final String hashedPassword) {
        String foo = new String(password);
        return BCrypt.checkpw(new String(password), hashedPassword);
    }
    
}
