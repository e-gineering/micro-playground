package com.github.nderwin.micro.playground.one.boundary;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("one")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("USER")
public class OneResource {

    @GET
    @Path("/")
    public Response get() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        
        job.add("one", "is the loneliest number that you'll ever do");
        
        return Response.ok(job.build()).build();
    }
}
