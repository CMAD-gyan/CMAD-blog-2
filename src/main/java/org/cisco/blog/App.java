package org.cisco.blog;

import java.sql.Timestamp;
import java.util.List;
import org.cisco.blog.*;
import org.cisco.blog.model.Answer;
import org.cisco.blog.model.Comment;
import org.cisco.blog.model.Question;
import org.cisco.blog.model.User;
import org.cisco.blog.model.Vote;
import org.cisco.blog.service.ServiceFactory;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Reference;

public class App {

	public static void main(String[] args) {
		
		Question ques = new Question("This is our new question", "We have ore questions cool", "HurraySingh", null);
		ques.setTitle("This is my new question");
		ques.setText("here we go lets handle the problems");
		Datastore dataStore = ServiceFactory.getMongoDB();
		dataStore.save(ques);
	}
}