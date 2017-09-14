package com.github.nderwin.micro.playground.security.boundary;

import java.io.IOException;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class CorsResponseFilter implements ContainerResponseFilter {

	@Override
	public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
		responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
		responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");

		responseContext.getHeaders().add("Access-Control-Allow-Methods", HttpMethod.DELETE 
				+ ", " + HttpMethod.GET 
				+ ", " + HttpMethod.HEAD
				+ ", " + HttpMethod.OPTIONS
				+ ", " + HttpMethod.POST
				+ ", " + HttpMethod.PUT
				+ ", PATCH"
		);
		
		responseContext.getHeaders().add("Access-Control-Allow-Headers", HttpHeaders.ACCEPT
				+ ", " + HttpHeaders.AUTHORIZATION
				+ ", " + HttpHeaders.CONTENT_TYPE
		);
	}
	
}
