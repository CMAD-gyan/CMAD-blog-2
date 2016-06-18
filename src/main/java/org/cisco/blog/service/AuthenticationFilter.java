package org.cisco.blog.service;

import java.io.IOException;
import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.bson.types.ObjectId;
import org.cisco.blog.model.Session;
import org.mongodb.morphia.Datastore;

import java.security.Principal;
import java.sql.Timestamp;


@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
	String username = null;
	//String id = null;

	void validateToken ( String token) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid =  new ObjectId(token);
		Session sess =  dataStore.get(Session.class, oid);
		if (sess == null) {
			throw new NotAuthorizedException("Not valid session");
		}
		username = sess.getUsername();
		sess.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
		//update the last active time 
		dataStore.save(sess);

	}

	public void filter(ContainerRequestContext requestContext) throws IOException {

		// Get the HTTP Authorization header from the request
		String authorizationHeader = 
				requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		// Check if the HTTP Authorization header is present and formatted correctly 
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new NotAuthorizedException("Authorization header must be provided");
		}

		// Extract the token from the HTTP Authorization header
		String token = authorizationHeader.substring("Bearer".length()).trim();

		try {
			// Validate the token
			validateToken(token);

		} catch (Exception e) {
			requestContext.abortWith(
					Response.status(Response.Status.UNAUTHORIZED).build());
		}

		requestContext.setSecurityContext(new SecurityContext() {
			//@Override
			public Principal getUserPrincipal() {
				return new Principal() {

					//@Override
					public String getName() {
						return username;
					}
				};

			}

			//@Override
			public boolean isUserInRole(String role) {
				return true;
			}

			//@Override
			public boolean isSecure() {
				return false;
			}

			//@Override
			public String getAuthenticationScheme() {
				return null;
			}
		});
	}
}

