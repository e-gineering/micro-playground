package com.github.nderwin.micro.playground.security.boundary;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Stateless
@Path("swagger")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class SwaggerResource {

    @GET
    @Path("/")
    public File get(@Context ServletContext context) throws MalformedURLException, URISyntaxException {
        URL url = context.getResource("/swagger.json");
        File f = new File(url.toURI());
        
        return f;
//        try (BufferedReader br = new BufferedReader(new FileReader("swagger.json"))) {
//            return Response.ok((Object) f)
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"swagger.json\"")
//                    .header(HttpHeaders.CONTENT_LENGTH, f.length())
//                    .header("Access-Control-Allow-Origin", "*")
//                    .header("Access-Control-Allow-Credentials", "true")
//                    .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
//                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS, HEAD")
//                    .build();
//        } catch (IOException ex) {
//            return Response.serverError().build();
//        }
    }
}
