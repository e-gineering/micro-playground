package com.github.nderwin.micro.playground.security.boundary;

import com.github.nderwin.micro.playground.security.jwt.TokenCredential;
import com.github.nderwin.micro.playground.security.control.BCryptPasswordHash;
import com.github.nderwin.micro.playground.security.jwt.TokenHandler;
import com.github.nderwin.micro.playground.security.entity.Caller;
import com.github.nderwin.micro.playground.security.entity.InvalidToken;
import java.net.URI;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityExistsException;
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
    TokenHandler tokenHandler;
    
    @POST
    @Path("/login")
    @PermitAll
    public Response login(final JsonObject body) {
        try {
            Caller c = em.createNamedQuery("Caller.findByUsername", Caller.class)
                    .setParameter("username", body.getString("username"))
                    .getSingleResult();
            
            if (passwordHash.verify(body.getString("password").toCharArray(), c.getPassword())) {
                String token = tokenHandler.createCredential(c);

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
            String token = tokenHandler.stripHeader(authHeader);
            TokenCredential credential = tokenHandler.retrieveCredential(token);
            
            InvalidToken it = new InvalidToken(token, credential.getExpirationDate());
            em.persist(it);

            return Response.ok().build();
        }
        
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    @GET
    @Path("/")
    public Response read(@Context HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        
        if (null != authHeader) {
            TokenCredential credential = tokenHandler.retrieveCredential(authHeader);
            
            return Response.ok(credential.toJson()).build();
        }
        
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
    @POST
    @Path("/")
    @PermitAll
    public Response create(final @Context HttpServletRequest request, final User user) {
        Caller c = new Caller(user.username, passwordHash.generate(user.password.toCharArray()));
        c.addRole("USER");
        
        try {
            em.persist(c);
        } catch (EntityExistsException ex) {
            return Response.status(Response.Status.CONFLICT).build();
        }
        
        return Response.created(URI.create(request.getRequestURI() + "/login")).build();
    }
    
}
