package org.cisco.blog.service;
import java.net.URI;
import java.util.ArrayList;
//import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.cisco.blog.model.*;

@Path("/questions")
public class QuestionService {
	
	
	@PUT
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public Response createUpdateQuestion(Question ques, 
 			@Context SecurityContext securityContext, 
 			@Context UriInfo uriInfo){
		Question question = null;
		User user;
		String username = securityContext.getUserPrincipal().getName();
		Datastore dataStore = ServiceFactory.getMongoDB();

		ques.setCreateTime();
		ques.setUpdateTime();
		ques.setUsername(username);

		question =  dataStore.find(Question.class).field("title").equal(ques.getTitle()).get();
		
		if (question == null) {
			user =  dataStore.find(User.class).field("username").equal(username).get();
			ques.setUser(user);
			dataStore.save(ques);
			ques.getUser().setPassword("xxxxxxxxx");
			URI uriOfCreatedResource = URI.create(uriInfo.getRequestUri() +"/" + ques.getId());
			return Response.status(Response.Status.CREATED).location(uriOfCreatedResource).entity(ques).build();
		} else {
			if (question.getUsername().equals(username)) {
				question.setText(ques.getText());
				question.setUpdateTime();
				dataStore.save(question);
				question.getUser().setPassword("xxxxxxxxx");
				return Response.status(Response.Status.OK).entity(question).build();
			} else {
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}
		}
	}
		
	@GET
	@Path("/{param}")
	@Produces({MediaType.APPLICATION_JSON})
    public Response getQuestionById(@PathParam("param") String id) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid;
		Question question = null;
		try {
			oid =  new ObjectId(id);
		} catch(Exception e) {
			return Response.status(Response.Status.NO_CONTENT).build();
		}
		
		question =  dataStore.get(Question.class, oid);
		
		if (question != null) {
			question.getUser().setPassword("xxxxxxxxx");
			//@TBD get some better way to fix
			for ( int i=0; i < question.getComments().size(); i++) {
				question.getComments().get(i).getUser().setPassword("XXXXX");
			}
			
			for ( int i=0; i < question.getAnswers().size(); i++) {
				question.getAnswers().get(i).getUser().setPassword("XXXXX");
			}
			question.setVotes(null);
			return Response.status(Response.Status.OK).entity(question).build();
		}
		return Response.status(Response.Status.NO_CONTENT).build();
	}
		
	
	@POST
	@Path("/search/length")
	@Consumes({MediaType.TEXT_PLAIN})
	@Produces({MediaType.TEXT_PLAIN})
	public int getLengthQuestionBySerch(String searchString) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		Query<Question> q = dataStore.createQuery(Question.class).search(searchString);
		int size  = q.asList().size();
		return size;
	}	
	
	@POST
	@Path("/search")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.TEXT_PLAIN})
    public Response getQuestionBySearch(String search_string,
			@QueryParam("offset") String offset,
			@QueryParam("length") String length) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		try {
			Query<Question> q = dataStore.createQuery(Question.class).search(search_string);
			List<Question> ques = null;
			
			if (offset == null && length == null) {
				ques = q.order("-viewCount").asList();
			} else {
				ques = q.offset(Integer.parseInt(offset)).limit(Integer.parseInt(length)).order("-viewCount").asList();
			}
			if (ques != null){
				for (int i = 0; i < ques.size(); i++) {
					//we should not send password
					ques.get(i).setAnswers(null);
					ques.get(i).setComments(null);
					ques.get(i).setVotes(null);
					ques.get(i).setUser(null);
				}
			}
			return Response.status(Response.Status.OK).entity(ques).build();
		}catch (Exception e) {
			return Response.status(Response.Status.NO_CONTENT).build();
		}
	}	
	
	@GET
	@Path("/length")
	@Produces({MediaType.TEXT_PLAIN})
	public int  getLengthAllQuestion() {
		Datastore dataStore = ServiceFactory.getMongoDB();
		Query<Question> q = dataStore.createQuery(Question.class);
		int size = q.asList().size();
		return size;
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	
	public Response getallQuestion( @QueryParam("offset") String offset,
									@QueryParam("length") String length){
		Datastore dataStore = ServiceFactory.getMongoDB();
		List<Question> ques = null;
		
		try {
			if (offset == null && length == null) {
				ques = dataStore.createQuery(Question.class).order("-viewCount").asList();
			} else {
				ques = dataStore.createQuery(Question.class).offset(Integer.parseInt(offset)).limit(Integer.parseInt(length)).order("-viewCount").asList();
			}
			if (ques != null){
				for (int i = 0; i < ques.size(); i++) {
					//we should not send password
					ques.get(i).setAnswers(null);
					ques.get(i).setComments(null);
					ques.get(i).setVotes(null);
					ques.get(i).setUser(null);
				}
				
				if (ques.size() > 0) {
					return Response.status(Response.Status.OK).entity(ques).build();
				}
			} 
			return Response.status(Response.Status.NO_CONTENT).build();
		} catch (Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	@DELETE
	@Secured
	@Path("/{param}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteQuestionById(@PathParam("param") String id,
			                       @Context SecurityContext securityContext) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid;
		Question question = null;
		
		try {
			oid =  new ObjectId(id);
		} catch(Exception e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		
		question =  dataStore.get(Question.class, oid);

		question =  dataStore.get(Question.class, oid);
		if (question != null){
			if( securityContext.getUserPrincipal().getName().equals("admin") || 
					securityContext.getUserPrincipal().getName().equals(
							             question.getUser().getUsername()))	{
				
				for ( int i=0; i < question.getAnswers().size(); i++) {
					ObjectId idAns =  new ObjectId(question.getAnswers().get(i).getId());
					dataStore.delete(Answer.class, idAns);
				}	
				dataStore.delete(Question.class, oid);
				return Response.status(Response.Status.OK).entity("Successfully Deleted").build();
				
			}else {
				return Response.status(Response.Status.UNAUTHORIZED).build();
			}	
		}
		return Response.status(Response.Status.OK).entity("Successfully Deleted").build();
	}

	//comments 
	//post & edit
	@POST
	@Secured
	@Path("/{param}/comments")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Question postComment(Comment com, 
			                @PathParam("param") String id,
            				@Context SecurityContext securityContext ) {
		int i,j=0;
		boolean update = false;
		Datastore dataStore = ServiceFactory.getMongoDB();
		String username = securityContext.getUserPrincipal().getName();
		ObjectId  oid = null;
		try {
			oid =  new ObjectId(id);
		} catch (Exception e) {
			throw new BadRequestException ("OID passed is not okay");
		}
		
		Question question =  dataStore.get(Question.class, oid);
		
		if (question == null){
			throw  new NotFoundException("Not found");
		}

		List <Comment> comment = question.getComments();
		
		for (i = 0; i < comment.size(); i++) {
			
			if (comment.get(i).getUsername().equals(username)) {
				update = true;
				j = i;
				break;
			}
		}
		
		if (update) {
			comment.get(j).setText(com.getText());
			comment.get(j).setUpdateTime();
			question.setComments(comment);
		} else {
			User user =  dataStore.find(User.class).field("username").equal(username).get();
			Comment newComment = new Comment(com.getText(), username,user);
			comment.add(newComment);
		}
		dataStore.save(question);
		return question;
	}
	
	@DELETE
	@Secured
	@Path("/{param}/comments")
	@Produces(MediaType.APPLICATION_JSON)
	public Question deleteComment(@PathParam("param") String id,
            					@Context SecurityContext securityContext ) {
		String username = securityContext.getUserPrincipal().getName();
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid = null;
		int i;
		boolean found = false;
		
		try {
			oid =  new ObjectId(id);
		} catch (Exception e) {
			throw new BadRequestException ("OID passed is not okay");
		}
		Question question =  dataStore.get(Question.class, oid);
		
		if (question != null) {
			List <Comment> comment = question.getComments();
				
			for (i = 0; i < comment.size(); i++) {
				if (comment.get(i).getUsername().equals(username)) {
					found = true;
					break;
				}
			}
			
			if (found == true) {
				comment.remove(i);
				question.setComments(comment);
				dataStore.save(question);
			}else {
				throw new BadRequestException ("Invalid comment");
			}
		} else {
			throw new BadRequestException ("Question not found");
		}
		return question;
	}
	
	
///Not tested ..........	
	@POST
	@Secured
	@Path("/{param}/vote")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Question postVote(Vote votein, @PathParam("param") String id,
            			 @Context SecurityContext securityContext ) {
		String username = securityContext.getUserPrincipal().getName();
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid = null;
		
		int i;
		int j = 0;
		int totalVote =0;
		boolean found = false;
		
		try {
			oid =  new ObjectId(id);
		} catch (Exception e) {
			throw new BadRequestException ("OID passed is not okay");
		}
		
		if (votein.getVote() > 1 || votein.getVote() < -1 ){
			throw new BadRequestException ("Invalid vote");
		}
		
		Question question =  dataStore.get(Question.class, oid);
		
		if (question == null){
			throw  new NotFoundException("Not found");
		}

		List <Vote> votes = question.getVotes();
		
		if (votes == null) 
			votes = new ArrayList <Vote>();
		
		for (i = 0; i < votes.size(); i++) {
			if (votes.get(i).getUsername().equals(username)) {
				found = true;
				j=i;
			}
			totalVote +=  votes.get(i).getVote();
		}
		
		totalVote += votein.getVote();
		
		question.setTotalVotes(totalVote);
		
		if (found == true) {
			votes.get(j).setVote( votein.getVote());
			question.setVotes(votes);
		} else {
			User user =  dataStore.find(User.class).field("username").equal(username).get();
			Vote vote = new Vote(votein.getVote(), username,user); 
			votes.add(vote);
			question.setVotes(votes);
		}
		
		dataStore.save(question);
		return question;
	}
}
