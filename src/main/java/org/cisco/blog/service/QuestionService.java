package org.cisco.blog.service;
import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.mongodb.morphia.Datastore;
import org.cisco.blog.model.*;

@Path("/question")
public class QuestionService {
	
	@POST
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String createQuestion(Question ques, 
								@Context SecurityContext securityContext){
		//boolean update = false;
		Question question = null;
		
		ques.setCreateTime();
		ques.setUpdateTime();
		
		String username = securityContext.getUserPrincipal().getName();
		User user;
		
		
		ques.setUsername(username);
		Datastore dataStore = ServiceFactory.getMongoDB();
		question =  dataStore.find(Question.class).field("title").equal(ques.getTitle()).get();
		
		try {
			
			//no need to validate the user for null
			if (question == null) {
				try {
					user =  dataStore.find(User.class).field("username").equal(username).get();
				} catch (Exception e) {
					throw  new NotAcceptableException("Unknown Error");
				}
				
				if (user == null) {
					throw  new NotAcceptableException("Unknown Error");
				}
				
				ques.setUser(user);
				dataStore.save(ques);
			} else {
				if (question.getUsername().equals(username)) {
					question.setText(ques.getText());
					question.setUpdateTime();
					dataStore.save(question);
				} else {
					 throw new NotAcceptableException("Already Present");
				}
			}
		}catch (Exception e) {
			throw new BadRequestException("Unknow Problem");
		}
		return "Ok";
	}
	
	@GET
	@Path("/{param}")
	@Produces({MediaType.APPLICATION_JSON})
	public Question getQuestionById(@PathParam("param") String id) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid =  new ObjectId(id);
		Question question =  dataStore.get(Question.class, oid);
		
		// if question is found increment view count 
		if (question != null) {
			question.setViewCount(question.getViewCount() + 1);
			dataStore.save(question);
		}
		return question;
	}
	
	@DELETE
	@Secured
	@Path("/{param}")
	public void deleteQuestionById(@PathParam("param") String id,
			                       @Context SecurityContext securityContext) {
		
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid =  new ObjectId(id);
		Question question =  dataStore.get(Question.class, oid);
		//if question matches username then allow delete
		
		if (question == null){
			throw  new NotFoundException("Not found");
		}
		
		if( securityContext.getUserPrincipal().getName().equals("admin") || 
				securityContext.getUserPrincipal().getName().equals(
						             question.getUser().getUsername()))	{
			dataStore.delete(Question.class, oid);
		}else {
			throw  new NotAuthorizedException("You Don't Have Permission");
		}
		return;
	}
		
	//FIXME add start and end
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/{offset}-{length}")
	public List<Question> getallQuestion(@PathParam("offset") String offset, 
			                             @PathParam("length") String length) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		List<Question> ques = dataStore.createQuery(Question.class).offset(Integer.parseInt(offset)).limit(Integer.parseInt(length)).order("-viewCount").asList();
		
		if (ques == null){
			throw  new NotFoundException("Not found");
		}	
		
		for (int i = 0; i < ques.size(); i++) {
			//we should not send password
			ques.get(i).setAnswers(null);
			ques.get(i).setComments(null);
			ques.get(i).setVotes(null);
			ques.get(i).setUser(null);
		}
		return ques;
	}
		
	//FIXME add start and end
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public List<Question> getQuestions( ) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		List<Question> ques = dataStore.createQuery(Question.class).order("-viewCount").asList();
		
		if (ques == null){
			throw  new NotFoundException("Not found");
		}
		
		for (int i = 0; i < ques.size(); i++) {
			//we should not send password
			ques.get(i).setAnswers(null);
			ques.get(i).setComments(null);
			ques.get(i).setVotes(null);
			ques.get(i).setUser(null);
		}
		return ques;
	}		
		
		
	//comments 
	//post & edit
	@POST
	@Secured
	@Path("/{param}/comment")
	@Consumes(MediaType.APPLICATION_JSON)
	public void postComment(Comment com, 
			                @PathParam("param") String id,
            				@Context SecurityContext securityContext ) {
		int i;
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
		
		if (comment == null) 
			comment = new ArrayList <Comment>();
		
		for (i = 0; i < comment.size(); i++) {
			
			if (comment.get(i).getUsername() == username) {
				update = true;
				break;
			}
		}
		
		if (update) {
			comment.get(i).setText(com.getText());
			comment.get(i).setUpdateTime();
		} else {
			User user =  dataStore.find(User.class).field("username").equal(username).get();
			Comment newComment = new Comment(com.getText(), username,user);
			comment.add(newComment);
		}
		dataStore.save(question);
	}
	
	
	@DELETE
	@Secured
	@Path("/{param}/comment")	
	public void deleteComment(@PathParam("param") String id,
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
		//check if the user exist then delete 
		
		if (question == null){
			throw  new NotFoundException("Not found");
		}

		List <Comment> comment = question.getComments();
		
		if (comment == null) 
			return;
		for (i = 0; i < comment.size(); i++) {
			
			if (comment.get(i).getUsername() == username) {
				found = true;
				break;
			}
		}
		
		if (found) {
			comment.remove(i);
			dataStore.save(question);
		}
	}
	
	
	
	@POST
	@Secured
	@Path("/{param}/vote")
	@Consumes(MediaType.APPLICATION_JSON)
	public void postVote(Vote votein, @PathParam("param") String id,
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
		Question question =  dataStore.get(Question.class, oid);
		
		if (question == null){
			throw  new NotFoundException("Not found");
		}

		List <Vote> votes = question.getVotes();
		
		if (votes == null) 
			votes = new ArrayList <Vote>();
		
		for (i = 0; i < votes.size(); i++) {
			
			if (votes.get(i).getUsername() == username) {
				found = true;
				j=i;
			}
			totalVote +=  votes.get(i).getVote();
		}
		
		totalVote /= votes.size();
		question.setAvgVotes(totalVote);
		
		if (found == true) {
			votes.get(j).setVote( votein.getVote());
		} else {
			User user =  dataStore.find(User.class).field("username").equal(username).get();
			Vote vote = new Vote(votein.getVote(), username,user  ); 
			votes.add(vote);
		}
		dataStore.save(question);
	}

}
