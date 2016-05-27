package org.cisco.blog.service;
import java.util.List;
import org.bson.types.ObjectId;
import javax.ws.rs.*;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import org.mongodb.morphia.Datastore;
import org.cisco.blog.model.User;

@Path("/user")
public class UserService {
	
	@GET
	//@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public List<User> getAllUser(/* @Context SecurityContext securityContext*/) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		List<User> user = dataStore.createQuery(User.class).order("-score").asList();
		return user;
	}

    //get user by Id
	@GET
	@Secured
	@Path("/{param}")
	@Produces({MediaType.APPLICATION_JSON})
	public User getUserById(@PathParam("param") String id,  
			                @Context SecurityContext securityContext) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid =  new ObjectId(id);
		User user = null;
		try {
			user =  dataStore.get(User.class, oid);
		} catch ( Exception e) {
			throw  new ForbiddenException("Not found Error");
		}

		//allow to return only if admin or self
		if ( securityContext.getUserPrincipal().getName().equals(user.getUserId()) ||
				securityContext.getUserPrincipal().getName().equals("admin")) {

			//clear the password
			user.setPassword(null);
			return user;
		} else {
			throw  new NotAuthorizedException("You Don't Have Permission");
		}
	}
	
	
//POST /CMAD-blog-2/rest/user HTTP/1.1
//Host: localhost:8080
//Content-Type: application/json
//Cache-Control: no-cache
//Postman-Token: d5878963-6fdc-b5fa-ea2d-4a9862b1592c
//
//{
//  "userId": "gyanranjan",
//  "password": "password",
//  "userName": "Gyan Ranjan",
//  "email": "gyanranjan@alpha.com"
//}	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void createUser(User user){
		user.setCreateTime();
		user.setUpdateTime();
		user.setScore(0);
		user.setUserId(user.getUserId().toLowerCase());
		
		//verify email id
		if (!user.isValidEmailAddress(user.getEmail())) {
			throw  new BadRequestException("Unknown Error");
		}

		System.out.println("User=" + user.getUserName() + user.getPassword() + user.getEmail());
		try {
			Datastore dataStore = ServiceFactory.getMongoDB();
			dataStore.save(user);
		} catch (com.mongodb.DuplicateKeyException e) {
			throw  new NotAcceptableException("Alreads exist");
		} catch ( Exception e) {
			throw  new ForbiddenException("Unknown Error");
		}
		return;
	}
	
	//delete user
	@DELETE
	@Secured
	@Path("/{param}")
	@Produces({MediaType.APPLICATION_JSON})
	public void deleteUserById(@PathParam("param") String id,  
			                @Context SecurityContext securityContext) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid =  new ObjectId(id);
		User user = null;
		try {
			user =  dataStore.get(User.class, oid);
		} catch ( Exception e) {
			throw  new ForbiddenException("Not found Error");
		}
		//allow to return only if admin or self
		if ( securityContext.getUserPrincipal().getName().equals(user.getUserId()) ||
				securityContext.getUserPrincipal().getName().equals("admin")) {
			dataStore.delete(User.class, oid);
		} else {
			throw  new NotAuthorizedException("You Don't Have Permission");
		}
	}
	
	//edit user TBD
	//not now
	
}