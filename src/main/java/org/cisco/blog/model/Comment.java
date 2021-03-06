package org.cisco.blog.model;

import java.sql.Timestamp;
import org.mongodb.morphia.annotations.Embedded; 
import org.mongodb.morphia.annotations.Reference; 

@Embedded
public class Comment {
	private String text;
	private Timestamp createTime;
	private Timestamp updateTime;
	private String username;
    @Reference
    private User user;

    public Comment(){
    	
    }

    public Comment(String text, String username, User user){
    	this.text     = text;
    	createTime    = new Timestamp(System.currentTimeMillis());;
    	updateTime    = new Timestamp(System.currentTimeMillis());;
    	this.username = username;
    	this.user     = user;
    	
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
}
