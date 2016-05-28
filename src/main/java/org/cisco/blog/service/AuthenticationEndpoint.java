package org.cisco.blog.service;

import java.sql.Timestamp;

import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.POST;
//import javax.ws.rs.FormParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
//import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.cisco.blog.model.Session;
//import org.bson.types.ObjectId;
import org.cisco.blog.model.User;
import org.mongodb.morphia.Datastore;

@Path("/authentication")
public class AuthenticationEndpoint {

    @POST
    @Produces("application/text")
    @Consumes("application/json")
    public Response authenticateUser( User user) {

        try {

            // Authenticate the user using the credentials provided
            authenticate(user.getUsername(), user.getPassword());

            // Issue a token for the user
            String token = issueToken(user.getUsername());

            // Return the token on the response
            return Response.ok(token).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }      
    }

    private void authenticate(String username, String password) throws Exception {
    	
    	Datastore dataStore = ServiceFactory.getMongoDB();
    	
    	//verify username and password 
    	User user = dataStore.createQuery(User.class).filter("username =", username ).get();    
    	
    	if (user == null ||  !password.equals(user.getPassword()) ) {
    		throw  new ForbiddenException("Not found Error");
    	}
    	
    }

    private String issueToken(String username) {
    	Datastore dataStore = ServiceFactory.getMongoDB();
    	
    	Session session = dataStore.createQuery(Session.class).filter("username =", username ).get();
    	
    	if (session == null) {
    		session = new Session();
    		session.setUsername(username);
    	}
    	
    	session.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
    	
    	dataStore.save(session);
    	
    	Session sess = dataStore.createQuery(Session.class).filter("username =", username ).get(); 
    	
    	return sess.getId();
    }
}