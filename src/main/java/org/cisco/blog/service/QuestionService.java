package org.cisco.blog.service;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.mongodb.morphia.Datastore;
import org.cisco.blog.model.*;
import org.cisco.blog.model.Comment;
import org.cisco.blog.model.Question;

//@Secured
@Path("/question")
public class QuestionService {
	
	@POST
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	public void createQuestion(Question ques, 
								@Context SecurityContext securityContext){
		ques.setCreateTime();
		ques.setUpdateTime();
		String username = securityContext.getUserPrincipal().getName();
		ques.setUsername(username);
		Datastore dataStore = ServiceFactory.getMongoDB();
		User user =  dataStore.find(User.class).field("username").equal(username).get();
		
		//no need to validate the user for null 
		ques.setUser(user);
		try {
			dataStore.save(ques);
		} catch (com.mongodb.DuplicateKeyException e) {
			throw new NotAcceptableException("Already Present");
		} catch (Exception e) {
			throw new BadRequestException("Unknow Problem");
		}
		return;
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
	@Path("/{offset}/{length}")
	public List<Question> getAllQuestion(@PathParam("offset") String offset, 
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
//	//Should be able to edit only if admin or the owner
//	//fixme
//	@PUT
//	@Secured
//	@Path("/{ObjectId}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces({MediaType.APPLICATION_JSON})
//	public Question updateQuestion(@PathParam("ObjectId") String ObjectId, 
//			                       Question ques,
//			                       @Context SecurityContext securityContext){
//		ObjectId  oid =  new ObjectId(ObjectId);
//		Datastore dataStore = ServiceFactory.getMongoDB();
//		Question question =  dataStore.get(Question.class, oid);
//		question.setUpdateTime();
//		question.setText(ques.getText());
//		question.setTitle(ques.getTitle());
//		System.out.println("Questions=" + ques.getTitle() + ques.getText()  );
//		dataStore.save(ques);
//		return ques;
//	}
}
