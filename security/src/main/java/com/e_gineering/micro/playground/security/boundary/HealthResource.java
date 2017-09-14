package com.e_gineering.micro.playground.security.boundary;

import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("health")
@PermitAll
public class HealthResource {

    private static final Logger LOG = Logger.getLogger(HealthResource.class.getName());
    
    @PersistenceContext
    EntityManager em;
    
    @GET
    @Path("/")
    public Response check() throws InterruptedException {
        if (null == em) {
            return Response.serverError().build();
        }
        
        LOG.info("Health check passed");
        return Response.ok().build();
    }
}
