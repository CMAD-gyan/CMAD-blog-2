package org.cisco.blog.service;

import java.sql.Timestamp;

import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.POST;
import javax.ws.rs.FormParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.cisco.blog.model.User;
import org.cisco.blog.model.Question;
import org.cisco.blog.model.Session;
import org.mongodb.morphia.Datastore;

@Path("/authentication")
public class AuthenticationEndpoint {
	

//	POST /CMAD-blog-2/rest/authentication HTTP/1.1
//	Host: localhost:8080
//	Cache-Control: no-cache
//	Postman-Token: a4ae85ec-19e2-b20c-96a7-a5ffa46840b1
//	Content-Type: application/x-www-form-urlencoded
//	username=gyanranjan&password=password

    @POST
    @Produces("application/json")
    @Consumes("application/x-www-form-urlencoded")
    public Response authenticateUser(@FormParam("username") String username, 
                                     @FormParam("password") String password) {

        try {

            // Authenticate the user using the credentials provided
            authenticate(username, password);

            // Issue a token for the user
            String token = issueToken(username);

            // Return the token on the response
            return Response.ok(token).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }      
    }

    private void authenticate(String username, String password) throws Exception {
    	
    	Datastore dataStore = ServiceFactory.getMongoDB();
    	
    	//verify username and password 
    	User user = dataStore.createQuery(User.class).filter("userId =", username ).get();    
    	
    	if (user == null ||  !password.equals(user.getPassword()) ) {
    		throw  new ForbiddenException("Not found Error");
    	}
    	
    }

    private String issueToken(String username) {
    	Datastore dataStore = ServiceFactory.getMongoDB();
    	
    	//check if already  there session
    	Session session = dataStore.createQuery(Session.class).filter("userId =", username ).get();
    	
    	if (session == null) {
    		session = new Session();
    		session.setUserId(username);
    	}
    	
    	session.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
    	
    	dataStore.save(session);
    	
    	Session sess = dataStore.createQuery(Session.class).filter("userId =", username ).get(); 
    			//dataStore.get(Session.class, oid);
    	
    	return sess.getId();
    }
}