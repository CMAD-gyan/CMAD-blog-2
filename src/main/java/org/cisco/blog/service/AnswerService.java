package org.cisco.blog.service;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bson.types.ObjectId;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
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

@Path("/answer")
public class AnswerService {
	@POST
	@Path("/{param}")
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String setAnswerByQuestionId( Answer ans,
			                           @PathParam("param") String id,
			                           @Context SecurityContext securityContext) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid = null;
		boolean update = false;
		int index;
		
		try {
			oid =  new ObjectId(id);
		} catch (Exception e) {
			throw new BadRequestException ("OID passed is not okay");
		}
		
		Question question =  dataStore.get(Question.class,oid);
		if (question == null){
			throw  new NotFoundException("Not found");
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
		return "Ok";
	}
	
	@DELETE
	@Secured
	@Path("/{questionId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String setAnswerByQuestionId(@PathParam("questionId") String id, @Context SecurityContext securityContext) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid = null;
		ObjectId  answerOid = null;
		try {
			oid =  new ObjectId(id);
		} catch (Exception e) {
			throw new BadRequestException ("OID passed is not okay");
		}
		
		Question question =  dataStore.get(Question.class, oid);
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
		return  "Ok";
	}
	
	
//comments 
//post & edit
	@POST
	@Secured
	@Path("/{param}/comment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String postComment(Comment com, 
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
		
		Answer ans =  dataStore.get(Answer.class, oid);
		
		if (ans == null){
			throw  new NotFoundException("Not found");
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
		return "Ok";
	}


	@DELETE
	@Secured
	@Path("/{param}/comment")
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
		
		if (votein.getVote() > 4 || votein.getVote() < 0 ){
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
		totalVote /= (votes.size() + 1);
		
		
		answer.setAvgVotes(totalVote);
		
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
