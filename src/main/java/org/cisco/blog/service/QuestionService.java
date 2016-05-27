package org.cisco.blog.service;
import java.util.List;
import org.bson.types.ObjectId;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
	
   // @Context
   // SecurityContext securityContext;
    
	
	@GET
	@Secured
	@Path("/{param}")
	@Produces({MediaType.APPLICATION_JSON})
	public Question getQuestionById(@PathParam("param") String id,  
			                       @Context SecurityContext securityContext) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid =  new ObjectId(id);
		Question question =  dataStore.get(Question.class, oid);
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
		//if question matchs username then allow delete
		dataStore.delete(Question.class, oid);
		
		return;
	}
	
	//FIXME add start and end
	@GET
	@Secured
	@Produces({MediaType.APPLICATION_JSON})
	public List<Question> getAllQuestion(@Context SecurityContext securityContext) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		List<Question> ques = dataStore.createQuery(Question.class).order("-viewCount").asList();
		//offset(0).limit(2).
		
		System.out.println("-----------------" + securityContext.getUserPrincipal().getName()  );
		return ques;
	}
	
	
	@POST
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	public void createQuestion(Question ques, 
								@Context SecurityContext securityContext){
		ques.setCreateTime();
		ques.setUpdateTime();
		System.out.println("Questions=" + ques.getTitle() + ques.getText()  );
		Datastore dataStore = ServiceFactory.getMongoDB();
		dataStore.save(ques);
		return;
	}
	
	
	@PUT
	@Secured
	@Path("/{ObjectId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.APPLICATION_JSON})
	public Question updateQuestion(@PathParam("ObjectId") String ObjectId, 
			                       Question ques,
			                       @Context SecurityContext securityContext){
		ObjectId  oid =  new ObjectId(ObjectId);
		Datastore dataStore = ServiceFactory.getMongoDB();
		Question question =  dataStore.get(Question.class, oid);
		question.setUpdateTime();
		question.setUpdateTime();
		question.setText(ques.getText());
		question.setTitle(ques.getTitle());
		System.out.println("Questions=" + ques.getTitle() + ques.getText()  );
		dataStore.save(ques);
		return ques;
	}
	
}
