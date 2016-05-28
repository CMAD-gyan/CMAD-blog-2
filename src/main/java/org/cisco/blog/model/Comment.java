package org.cisco.blog.model;

import java.sql.Timestamp;
//import org.mongodb.morphia.annotations.Entity; 
import org.mongodb.morphia.annotations.Embedded; 
import org.mongodb.morphia.annotations.Reference; 
import org.mongodb.morphia.annotations.Id; 

@Embedded
public class Comment {
	private String text;
	private Timestamp createTime;
	private Timestamp updateTime;
	private String userName;
    @Reference
    private User user;

    public Comment(){
    	
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
	
	
    
}
