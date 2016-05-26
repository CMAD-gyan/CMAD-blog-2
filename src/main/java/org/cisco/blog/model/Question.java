package org.cisco.blog.model;


import java.sql.Timestamp;
import java.util.List;

import org.bson.types.ObjectId;
import org.cisco.blog.model.VoteType;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.utils.IndexDirection;

@Entity
public class Question {
	@Id
    private ObjectId id;
	
	@Indexed(value=IndexDirection.ASC, name="title", unique=true, dropDups=true)
	private String title;
	private String text;

	private Timestamp createTime;
	private Timestamp updateTime;

	private String userName;
	private int    avgVotes;
	private int    viewCount;
@Embedded
	private List<Vote> votes;

@Embedded
	private List<Comment>  comments;

@Embedded
	private List<Answer>  answers;


	public void QuestionCleanEmbeds(){
		this.votes       = null;
		this.comments    = null;
		this.answers     = null;
	}

	public Question() {
		this.votes       = null;
		this.comments    = null;
		this.answers     = null;
		this.avgVotes    = VoteType.GOOD;
		this.viewCount   = 0;		
	}
	
	public Question(String title, String text, String userName, User user) {
		this.title = title;
		this.text  = text;
	   //@toDo	
		//this.user  = user;
		this.userName  = userName;		
		this.createTime  = new Timestamp(System.currentTimeMillis());
		this.updateTime  = new Timestamp(System.currentTimeMillis());
		this.votes       = null;
		this.comments    = null;
		this.answers     = null;
		this.avgVotes    = VoteType.GOOD;
		this.viewCount   = 0;
	}
	
	public String getId(){
		return this.id.toHexString();
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getText(){
		return this.text;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	
	public Timestamp getCreateTime(){
		return this.createTime;
	}
	
	public void setCreateTime(Timestamp createTime){
		this.createTime = createTime;
	}
	
	public void setCreateTime(){
		this.createTime = new Timestamp(System.currentTimeMillis());
	}
	
	public Timestamp getUpdateTime(){
		return this.updateTime;
	}
	
	public void setUpdateTime(Timestamp updateTime){
		this.updateTime = updateTime;
	}
	
	public void setUpdateTime(){
		this.updateTime = new Timestamp(System.currentTimeMillis());
	}
	
	public String getUserName(){
		return this.userName;
	}
	
	public void setUserName (String userName){
		this.userName = userName;
	}
	
//	public User getUser(){
//		return this.user;
//	}
//	
//	public void setUser(User user){
//		this.user = user;
//	}
	
	public List< Vote > getVotes() { return votes; }
	public void setVotes( List< Vote > votes ) { this.votes = votes; }
	
	public List< Comment > getComments() { return comments; }
	public void setComments( List< Comment > comments ) { this.comments = comments; }
	
	public List< Answer > getAnswers() { return answers; }
	public void setAnswers( List< Answer > answers ) { this.answers = answers; }

}

