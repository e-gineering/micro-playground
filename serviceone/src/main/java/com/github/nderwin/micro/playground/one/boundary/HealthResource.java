package com.github.nderwin.micro.playground.one.boundary;

import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
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
    
    @GET
    @Path("/")
    public Response check() throws InterruptedException {
        LOG.info("Health check passed");
        return Response.ok().build();
    }
}
