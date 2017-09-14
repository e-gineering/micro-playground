package com.e_gineering.micro.playground.security.boundary;

import com.e_gineering.micro.playground.security.control.BCryptPasswordHash;
import com.e_gineering.micro.playground.security.control.TokenIdentityStore;
import com.e_gineering.micro.playground.security.entity.Caller;
import java.net.URI;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Stateless
@Path("/authentication")
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class AuthenticationResource {

    @PersistenceContext
    EntityManager em;
    
    @Inject
    BCryptPasswordHash passwordHash;
    
    @Inject
    TokenIdentityStore identityStore;
    
    @POST
    @Path("/login")
    @PermitAll
    public Response login(final JsonObject body) {
        try {
            Caller c = em.createNamedQuery("Caller.findByUsername", Caller.class)
                    .setParameter("username", body.getString("username"))
                    .getSingleResult();
            
            if (passwordHash.verify(body.getString("password").toCharArray(), c.getPassword())) {
                String token = identityStore.createCredential(c);

                if (null == token) {
                    return Response.serverError().build();
                }

                JsonObjectBuilder job = Json.createObjectBuilder();
                return Response.ok(job.add("token", token).build()).build();
            }
            
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (NoResultException | NonUniqueResultException ex) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
    @GET
    @PUT
    @POST
    @DELETE
    @PATCH
    @Path("/logout")
    public Response logout(@Context HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (null != authHeader) {
            identityStore.invalidate(authHeader);
            
            return Response.ok().build();
        }
        
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    @GET
    @Path("/")
    public Response read(@Context SecurityContext context) {
        JsonObjectBuilder job = Json.createObjectBuilder();
        
        job.add("subject", context.getUserPrincipal().getName());

        Caller c = em.createNamedQuery("Caller.findByUsername", Caller.class)
                .setParameter("username", context.getUserPrincipal().getName())
                .getSingleResult();
        
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (String s : c.getRoles()) {
            jab.add(s);
        }
        
        job.add("scope", jab.build());
        
        return Response.ok(job.build()).build();
    }
    
    @POST
    @Path("/")
    @PermitAll
    public Response create(final @Context HttpServletRequest request, final User user) {
        Caller c = new Caller(user.username, passwordHash.generate(user.password.toCharArray()));
        c.addRole("USER");
        
        em.persist(c);
        
        return Response.created(URI.create(request.getRequestURI() + "/login")).build();
    }
    
}
