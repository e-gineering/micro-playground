package com.github.nderwin.micro.playground.security.control;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class InvalidTokenCacheTimer {
    
    private static final Logger LOG = Logger.getLogger(InvalidTokenCacheTimer.class.getName());
    
    @PersistenceContext
    EntityManager em;
    
    @Lock(LockType.READ)
    @Schedule(persistent = false, hour = "*", minute = "*/15")
    public void execute() {
        int count = em.createNamedQuery("InvalidToken.deleteExpiredTokens").executeUpdate();
        
        LOG.log(Level.INFO, "Invalid cached tokens removed: {0}", count);
    }
}
