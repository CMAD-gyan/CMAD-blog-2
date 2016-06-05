package org.cisco.blog.model;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.cisco.blog.model.VoteType;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.utils.IndexDirection;
import org.mongodb.morphia.utils.IndexType;

@Entity
@Indexes({
@Index(fields = @Field(value = "$**", type = IndexType.TEXT))
})

public class Question {
	@Id
    private ObjectId id;
	
	@Indexed(value=IndexDirection.ASC, name="title", unique=true, dropDups=true)
	private String title;
	private String text;

	private Timestamp createTime;
	private Timestamp updateTime;

@Reference	
	private User   user;
	private String username;
	private int    avgVotes;
	private int    viewCount;
@Embedded
	private List<Vote> votes;

@Embedded
	private List<Comment>  comments;

@Reference
	private List<Answer>  answers;

	public Question() {
		this.votes       = new ArrayList <Vote>();
		this.comments    = new ArrayList <Comment>();
		this.answers     = new ArrayList <Answer>();
		this.avgVotes    = VoteType.GOOD;
		this.viewCount   = 0;		
	}
	
	public Question(String title, String text, String username, User user) {
		this.title = title;
		this.text  = text;

		this.user  = user;
		this.username  = username;		
		this.createTime  = new Timestamp(System.currentTimeMillis());
		this.updateTime  = new Timestamp(System.currentTimeMillis());
		this.votes       = new ArrayList <Vote>();
		this.comments    = new ArrayList <Comment>();
		this.answers     = new ArrayList <Answer>();
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
	
	public String getUsername(){
		return this.username;
	}
	
	public void setUsername (String username){
		this.username = username;
	}
	
	public User getUser(){
		return this.user;
	}
	
	public void setUser(User user){
		this.user = user;
	}
	
	public int getViewCount(){
		return this.viewCount;
	}
	
	public void setViewCount(int viewCount){
		this.viewCount = viewCount;
	}
	
	public int getAvgVotes(){
		return this.avgVotes;
	}
	
	public void setAvgVotes(int avgVotes){
		this.avgVotes = avgVotes;
	}
	
	public List< Vote > getVotes() { return votes; }
	public void setVotes( List< Vote > votes ) { this.votes = votes; }
	
	public List< Comment > getComments() { return comments; }
	public void setComments( List< Comment > comments ) { this.comments = comments; }
	
	public List< Answer > getAnswers() { return answers; }
	public void setAnswers( List< Answer > answers ) { this.answers = answers; }

}
