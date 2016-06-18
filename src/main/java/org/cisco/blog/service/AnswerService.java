package org.cisco.blog.service;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bson.types.ObjectId;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.cisco.blog.model.*;

@Path("/answers")
public class AnswerService {
	
	private void fixQuestionDisplay(Question question)
	{
		question.getUser().setPassword("xxxxxxxxx");
		for ( int i=0; i < question.getComments().size(); i++) {
			question.getComments().get(i).getUser().setPassword("XXXXX");
		}
		
		for ( int i=0; i < question.getAnswers().size(); i++) {
			question.getAnswers().get(i).getUser().setPassword("XXXXX");
		}
		question.setVotes(null);
	}
	
	
	@PUT
	@Secured
	@Path("/{param}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
    public Response setAnswerByQuestionId(Answer ans,
			                           @PathParam("param") String id,
			                           @Context SecurityContext securityContext) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid = null;
		boolean update = false;
		int index;
		
		try {
			oid =  new ObjectId(id);
		} catch (Exception e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		Question question =  dataStore.get(Question.class,oid);
		if (question == null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		String username = securityContext.getUserPrincipal().getName();
		User user =  dataStore.find(User.class).field("username").equal(username).get();
		
		List< Answer > answers = question.getAnswers();
		
		if ( answers == null ) 
			answers = new ArrayList <Answer>();
		
		//iterate to see of user had already answered something  here 

		for (index = 0; index < answers.size(); index++) {
			if (answers.get(index).getUserName().equals(username)) {
				update = true;
				break;
			}
		}

		if (update) {
			answers.get(index).setText(ans.getText());
			answers.get(index).setUpdateTime();
			
		} else {
			Answer answer = new Answer(ans.getText(), username, user);
			answers.add(answer);
			question.setAnswers(answers);
		}
		
		dataStore.save(answers);
		dataStore.save(question);
		return Response.status(Response.Status.OK).entity(question).build();
	}
	
	@DELETE
	@Secured
	@Path("/{questionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setAnswerByQuestionId(@PathParam("questionId") String id, @Context SecurityContext securityContext) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid = null;
		ObjectId  answerOid = null;
		try {
			oid =  new ObjectId(id);
		} catch (Exception e) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		Question question =  dataStore.get(Question.class, oid);
		if (question == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		String username = securityContext.getUserPrincipal().getName();
		
		if (question.getAnswers() != null) {
			List<Answer> list = question.getAnswers();
			for (Iterator<Answer> iterator = list.iterator(); iterator.hasNext();) {
				Answer answer = iterator.next();
			    if (answer.getUserName().equals(username)) {
			    	answerOid = new ObjectId(answer.getId());
			    	iterator.remove();
			    	
			    }
			}
		}
		dataStore.save(question);
		if (answerOid != null) {
			dataStore.delete(Answer.class, answerOid);
		}
		fixQuestionDisplay(question);
		return Response.status(Response.Status.OK).entity(question).build();
	}
	

	
//comments 
//post & edit
	@PUT
	@Secured
	@Path("/{param}/comments")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	
	public Response postComment(Comment com, 
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
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		
		Answer ans =  dataStore.get(Answer.class, oid);
		
		//Question question =  dataStore.find(Question.class).field("title").equal(ques.getTitle()).get();
		
		
		if (ans == null){
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	
		List <Comment> comment = ans.getComments();
		
		if (comment == null) {
			comment = new ArrayList <Comment>();
		}
		
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
			ans.setComments(comment);
		} else {
			User user =  dataStore.find(User.class).field("username").equal(username).get();
			Comment newComment = new Comment(com.getText(), username,user);
			comment.add(newComment);
			ans.setComments(comment);
		}
		
		
		dataStore.save(ans);
		Question q = dataStore.createQuery(Question.class).field("answers").hasThisElement(ans).get();
		
		if (q == null) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}
		fixQuestionDisplay(q);
		
		if (update) {
			return Response.status(Response.Status.OK).entity(q).build();
		} else {
			return Response.status(Response.Status.CREATED).entity(q).build();
		}
	}


	@DELETE
	@Secured
	@Path("/{param}/comments")
	@Produces(MediaType.TEXT_PLAIN)
	public String deleteComment(@PathParam("param") String id,
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
		Answer anwser =  dataStore.get(Answer.class, oid);
		
		if (anwser != null) {
			List <Comment> comment = anwser.getComments();
			
			if (comment != null) { 
				for (i = 0; i < comment.size(); i++) {
					if (comment.get(i).getUsername().equals(username)) {
						found = true;
						break;
					}
				}
				
				if (found == true) {
					comment.remove(i);
					anwser.setComments(comment);
					dataStore.save(anwser);
				}
			}
		}
		return "Ok";
	}


	///Not tested ..........	
	@POST
	@Secured
	@Path("/{param}/vote")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String postVote(Vote votein, @PathParam("param") String id,
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
		
		Answer answer =  dataStore.get(Answer.class, oid);
		
		if (answer == null){
			throw  new NotFoundException("Not found");
		}
	
		List <Vote> votes = answer.getVotes();
		

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
		
		answer.setTotalVotes(totalVote);
		
		if (found == true) {
			votes.get(j).setVote( votein.getVote());
			answer.setVotes(votes);
		} else {
			User user =  dataStore.find(User.class).field("username").equal(username).get();
			Vote vote = new Vote(votein.getVote(), username,user); 
			votes.add(vote);
			answer.setVotes(votes);
		}
		
		dataStore.save(answer);
		return "Ok";
	}
	
	
	
}
