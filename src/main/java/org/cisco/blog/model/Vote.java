package org.cisco.blog.model;
import java.sql.Timestamp;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Reference; 

final class VoteType {
    public static final int DOWNVOTE =  -1;
    public static final int NOVOTE   =  0;
    public static final int UPVOTE =    1;
}

@Embedded
public class Vote {
    
//   @Indexed(value=IndexDirection.ASC, name="userName", unique=true, dropDups=true)
    private String username;
    
    @Reference
    private User user;
    
    private int vote;
    
	private Timestamp createTime;
	private Timestamp updateTime;
	
	public Vote () {		
	}
	
    public Vote(int vote, String username, User user){
    	this.vote     	= vote;
    	this.createTime = new Timestamp(System.currentTimeMillis());;
    	this.updateTime = new Timestamp(System.currentTimeMillis());;
    	this.username   = username;
    	this.user       = user;
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
	
	public int getVote(){
		return this.vote;
	}
	
	public void setVote(int vote){
		this.vote = vote;
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

}
