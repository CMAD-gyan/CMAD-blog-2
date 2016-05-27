package org.cisco.blog.service;
import java.util.ArrayList;
import java.util.Iterator;
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

@Path("/answer")
public class AnswerService {
	@POST
	@Path("/{param}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void setAnswerByQuestionId(Answer ans, @PathParam("param") String id) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid =  new ObjectId(id);
		Question question =  dataStore.get(Question.class,oid);
		
		//check if the user has alread question    
		//if yes then update else create
		
		Answer answer = new Answer(ans.getText(), ans.getUserName(), null);
		List< Answer > answers = question.getAnswers();
		if ( answers == null ) 
			answers = new ArrayList <Answer>();
		answers.add(answer);
		question.setAnswers(answers);
		dataStore.save(question);
		return;
	}
	

	@DELETE
	@Path("/{questionId}/{user}")
	public void setAnswerByQuestionId(@PathParam("questionId") String id,@PathParam("user") String username) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid =  new ObjectId(id);
		Question question =  dataStore.get(Question.class,oid);
		
		if (question.getAnswers() == null) {
			return;
		}
		List<Answer> list = question.getAnswers();
		for (Iterator<Answer> iterator = list.iterator(); iterator.hasNext();) {
			Answer answer = iterator.next();
		    if (answer.getUserName().equals(username)) {
		    	iterator.remove();
		    }
		}
		
		dataStore.save(question);
		return;
	}

}
