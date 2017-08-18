package com.github.nderwin.micro.playground.two.boundary;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("two")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class TwoResource {
    
    private static final Logger LOG = Logger.getLogger(TwoResource.class.getName());

    @GET
    @Path("/")
    public Response get(@Context HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        JsonValue oneResponse = JsonObject.NULL;
        
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://serviceone:8080/serviceone/resources/one");
        try {
            JsonObject response = target
                    .request(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .get(JsonObject.class);

            oneResponse = response.get("one");
        } catch (ProcessingException | WebApplicationException ex) {
            LOG.log(Level.WARNING, ex.getMessage(), ex);
        }

        JsonObjectBuilder job = Json.createObjectBuilder();
        
        job.add("one", oneResponse);
        job.add("two", "can be as bad as one");
        
        return Response.ok(job.build()).build();
    }
}
