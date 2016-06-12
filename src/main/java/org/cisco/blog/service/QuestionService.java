package org.cisco.blog.service;
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
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.cisco.blog.model.*;

@Path("/question")
public class QuestionService {
	@POST
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String createQuestion(Question ques, 
								@Context SecurityContext securityContext){
		Question question = null;
		User user;
		String username = securityContext.getUserPrincipal().getName();
		Datastore dataStore = ServiceFactory.getMongoDB();
		String id=null;
		
		ques.setCreateTime();
		ques.setUpdateTime();
		ques.setUsername(username);
		
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
				id = ques.getId();
			} else {
				if (question.getUsername().equals(username)) {
					question.setText(ques.getText());
					question.setUpdateTime();
					dataStore.save(question);
					id = question.getId();
				} else {
					 throw new NotAcceptableException("Already Present");
				}
			}
		}catch (Exception e) {
			throw new BadRequestException("Unknow Problem");
		}
		return id;
	}
	
		
	@GET
	@Path("/{param}")
	@Produces({MediaType.APPLICATION_JSON})
	public Question getQuestionById(@PathParam("param") String id) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid;
		Question question = null;
		
		try {
			oid =  new ObjectId(id);
		} catch(Exception e) {
			throw new BadRequestException("Bad param passed");
		}
		
		question =  dataStore.get(Question.class, oid);
		
		// if question is found increment view count 
		if (question != null) {
			question.setViewCount(question.getViewCount() + 1);
			dataStore.save(question);
			question.getUser().setPassword("xxxxxxxxx");
			
			//@TBD get some better way to fix
			for ( int i=0; i < question.getComments().size(); i++) {
				question.getComments().get(i).getUser().setPassword("XXXXX");
			}
			
			for ( int i=0; i < question.getAnswers().size(); i++) {
				question.getAnswers().get(i).getUser().setPassword("XXXXX");
			}
			question.setVotes(null);
		}
		return question;
	}
	
	@POST
	@Path("/search/{offset}-{length}")
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.TEXT_PLAIN})
	public List<Question> getQuestionBySerch(String search_string, @PathParam("offset") String offset, 
            @PathParam("length") String length) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid;
		Question question = null;
		
		
		Query<Question> q = dataStore.createQuery(Question.class).search(search_string);
		List<Question> ques = q.offset(Integer.parseInt(offset)).limit(Integer.parseInt(length)).order("-viewCount").asList();;
		
		if (ques != null){
			for (int i = 0; i < ques.size(); i++) {
				//we should not send password
				ques.get(i).setAnswers(null);
				ques.get(i).setComments(null);
				ques.get(i).setVotes(null);
				ques.get(i).setUser(null);
			}
		}
		return ques;
	}	

	
	
	@DELETE
	@Secured
	@Path("/{param}")
	@Produces(MediaType.TEXT_PLAIN)
	public String deleteQuestionById(@PathParam("param") String id,
			                       @Context SecurityContext securityContext) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid;
		Question question = null;
		
		try {
			oid =  new ObjectId(id);
		} catch(Exception e) {
			throw new BadRequestException("Bad param passed");
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
				
			}else {
				throw  new NotAuthorizedException("You Don't Have Permission");
			}	
		}
		return "Ok";
	}
		
	
	
	//FIXME add start and end
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/{offset}-{length}")
	public List<Question> getallQuestion(@PathParam("offset") String offset, 
			                             @PathParam("length") String length) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		List<Question> ques = dataStore.createQuery(Question.class).offset(Integer.parseInt(offset)).limit(Integer.parseInt(length)).order("-viewCount").asList();
		
		if (ques != null){
			for (int i = 0; i < ques.size(); i++) {
				//we should not send password
				ques.get(i).setAnswers(null);
				ques.get(i).setComments(null);
				ques.get(i).setVotes(null);
				ques.get(i).setUser(null);
			}
		}
		return ques;
	}
		
	//FIXME add start and end
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public List<Question> getQuestions( ) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		List<Question> ques = dataStore.createQuery(Question.class).order("-viewCount").asList();
		
		if (ques != null) {
			for (int i = 0; i < ques.size(); i++) {
				//we should not send password
				ques.get(i).setAnswers(null);
				ques.get(i).setComments(null);
				ques.get(i).setVotes(null);
				ques.get(i).setUser(null);
			}
		}
		return ques;
	}		
		
		
	//comments 
	//post & edit
	@POST
	@Secured
	@Path("/{param}/comment")
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
	@Path("/{param}/comment")
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
