package com.github.nderwin.micro.playground.security.boundary;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("/authentication")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class AuthenticationResource {

    private static final Logger LOG = Logger.getLogger(AuthenticationResource.class.getName());
    
    @PersistenceContext
    EntityManager em;
    
    @POST
    @Path("/login")
    @PermitAll
    public Response login(final JsonObject body) {
        try {
            // TODO - build and return a JWT
            JsonObjectBuilder job = Json.createObjectBuilder();
            return Response.ok(
                    job.add(
                        "token", 
                        Base64.getEncoder().encodeToString(
                            (body.getString("username") + ":" + body.getString("password")).getBytes("UTF-8")
                        )
                    ).build()
            ).build();
        } catch (NoResultException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (UnsupportedEncodingException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return Response.serverError().build();
        }
    }
    
    @GET
    @PUT
    @POST
    @DELETE
    // TODO @PATCH
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) {
        // TODO - track the invalidated JWTs
        return Response.ok().build();
    }
}
