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
	public void setAnswerByQuestionId( Answer ans,
			                           @PathParam("param") String id,
			                           @Context SecurityContext securityContext) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid = null;
		boolean update = false;
		int i;
		
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

		for (i = 0; i < answers.size(); i++) {
			if (answers.get(i).getUserName().equals(username)) {
				update = true;
				break;
			}
		}

		if (update) {
			answers.get(i).setText(ans.getText());
			answers.get(i).setUpdateTime();
			
		} else {
			Answer answer = new Answer(ans.getText(), username, user);
			answers.add(answer);
			question.setAnswers(answers);
		}
		
		dataStore.save(question);
		return;
	}
	
	@DELETE
	@Secured
	@Path("/{questionId}")
	public void setAnswerByQuestionId(@PathParam("questionId") String id, @Context SecurityContext securityContext) {
		Datastore dataStore = ServiceFactory.getMongoDB();
		ObjectId  oid = null;
		try {
			oid =  new ObjectId(id);
		} catch (Exception e) {
			throw new BadRequestException ("OID passed is not okay");
		}
		
		Question question =  dataStore.get(Question.class, oid);
		String username = securityContext.getUserPrincipal().getName();
		
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
