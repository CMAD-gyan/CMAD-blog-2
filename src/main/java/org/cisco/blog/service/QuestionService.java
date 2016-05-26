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
import javax.ws.rs.core.MediaType;

import org.mongodb.morphia.Datastore;
import org.cisco.blog.model.*;
import org.cisco.blog.model.Comment;
import org.cisco.blog.model.Question;

@Path("/question")
public class QuestionService {
	
	@GET
	@Path("/{param}")
	@Produces({MediaType.APPLICATION_JSON})
	public Question getQuestionById(@PathParam("param") String id) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid =  new ObjectId(id);
		Question question =  dataStore.get(Question.class, oid);
		return question;
	}
	
	@DELETE
	@Path("/{param}")
	public void deleteQuestionById(@PathParam("param") String id) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid =  new ObjectId(id);
		Question question =  dataStore.get(Question.class, oid);
		
		//if question matchs username then allow delete
		dataStore.delete(Question.class, oid);
		
		return;
	}
	
	//FIXME add start and end
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public List<Question> getAllQuestion() {
		Datastore dataStore = ServiceFactory.getMongoDB();
		List<Question> ques = dataStore.createQuery(Question.class).order("-viewCount").offset(0).limit(2).asList();
		
		//    .offset(1)
	    //.limit(10)
	    //.asList()
		//
		
		return ques;
	}
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void createQuestion(Question ques){
		ques.setCreateTime();
		ques.setUpdateTime();
		System.out.println("Questions=" + ques.getTitle() + ques.getText()  );
		Datastore dataStore = ServiceFactory.getMongoDB();
		dataStore.save(ques);
		return;
	}
	
	
	@PUT
	@Path("/{ObjectId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.APPLICATION_JSON})
	public Question updateQuestion(@PathParam("ObjectId") String ObjectId, Question ques){
		
		//ques.setUpdateTime();
		ObjectId  oid =  new ObjectId(ObjectId);

		Datastore dataStore = ServiceFactory.getMongoDB();
		Question question =  dataStore.get(Question.class, oid);
		question.setUpdateTime();
		
		
		question.setUpdateTime();
		question.setText(ques.getText());
		question.setTitle(ques.getTitle());
		
		

		
		
		System.out.println("Questions=" + ques.getTitle() + ques.getText()  );
		//Datastore dataStore = ServiceFactory.getMongoDB();
		dataStore.save(ques);
		return ques;
	}
	
	//Update only of Admin or Owner
	
}
