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
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public List<User> getAllUser( @Context SecurityContext securityContext) {
		
		if (!securityContext.getUserPrincipal().getName().equals("admin"))
			throw  new NotAuthorizedException("You Don't Have Permission");
			
		Datastore dataStore = ServiceFactory.getMongoDB();
		List<User> user = dataStore.createQuery(User.class).order("-score").asList();
		
		for (int i = 0; i < user.size(); i++) {
			user.get(i).setPassword(null);
		}
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
		User user = null;

		try {
			ObjectId  oid =  new ObjectId(id);
			user =  dataStore.get(User.class, oid);
		} catch ( Exception e) {
			throw  new ForbiddenException("Not found Error");
		}
		
		if (user == null) {
			throw  new NotAuthorizedException("Invalid user");
		}

		if ( securityContext.getUserPrincipal().getName().equals(user.getUsername()) ||
				securityContext.getUserPrincipal().getName().equals("admin")) {
			//clear the password
			user.setPassword("xxxxxxxx");
			return user;
		} else {
			throw  new NotAuthorizedException("You Don't Have Permission");
		}
	}
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/text")
	public String createUser(User user){
		//check if create of update
		//boolean update = false;
		Datastore dataStore = ServiceFactory.getMongoDB();
		
		if(user.getUsername() == null)
			throw  new BadRequestException("Invalid Email Error");
		
		user.setUsername(user.getUsername().toLowerCase());
	
		
		User userdb = dataStore.find(User.class).field("username").equal(
				                user.getUsername()).get();
		
		if (userdb == null) {
			//New user Create
			if ( user.getPassword() == null ||
					user.getEmail() ==null     || 
					!user.isValidEmailAddress(user.getEmail()) ) {
				throw  new BadRequestException("Invalid Email Error");
			}
			
			user.setCreateTime();
			user.setUpdateTime();
			user.setScore(0);
			try {
				dataStore.save(user);
			} catch ( Exception e) {
				throw  new BadRequestException("Unknown Error");
			}
		} else {
			//Old user either password change or email change
			if ( (user.getPassword() == null) && (user.getEmail() == null) )
				throw  new BadRequestException("Unknown Error");
				
			
			if (user.getPassword() != null) 
				userdb.setPassword(user.getPassword());
			
			if (user.getEmail() != null && user.isValidEmailAddress(user.getEmail()))
				userdb.setEmail(user.getEmail());	
			
			userdb.setUpdateTime();
			try {
				dataStore.save(userdb);
			} catch ( Exception e) {
				throw  new BadRequestException("Unknown Error");
			}
		}
		return "Ok";
	}

	
	//delete user
	@DELETE
	@Secured
	@Path("/{param}")
	@Produces("application/text")
	public String deleteUserById(@PathParam("param") String id,  
			                @Context SecurityContext securityContext) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid = null;
		User user = null;
		
		try {
			oid = new ObjectId(id);
			user =  dataStore.get(User.class, oid);
		} catch ( Exception e) {
			throw  new ForbiddenException("Not found Error");
		}
		
		if (user == null) {
			throw  new ForbiddenException("Not found Error");
		}

		//allow to return only if admin or self
		if ( securityContext.getUserPrincipal().getName().equals(user.getUsername()) ||
				securityContext.getUserPrincipal().getName().equals("admin")) {
			dataStore.delete(User.class, oid);
		} else {
			throw  new NotAuthorizedException("You Don't Have Permission");
		}
		return "Ok";
	}
}