package org.cisco.blog.model;
import java.sql.Timestamp;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Indexed;
//import org.mongodb.morphia.annotations.Entity; 
//import org.mongodb.morphia.annotations.Embedded; 
import org.mongodb.morphia.annotations.Reference; 
//import org.mongodb.morphia.annotations.Id; 
import org.mongodb.morphia.utils.IndexDirection;

final class VoteType {
    public static final int POOR =  0;
    public static final int GOOD =  1;
    public static final int VGOOD = 2;
    public static final int SUPER = 3;
    public static final int BEST  = 4;
}

@Embedded
public class Vote {
//    private ObjectId id;
    
//   @Indexed(value=IndexDirection.ASC, name="userName", unique=true, dropDups=true)
    private String userName;
    
//    @Reference
//    private User user;
    
    private VoteType vote;
    
	private Timestamp createTime;
	private Timestamp updateTime;
	
	public Vote () {		
	}
	
//	public ObjectId getId(){
//		return this.id;
//	}
//	
//	public void setId (ObjectId id){
//		this.id = id;
//	}
	
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
	
	public VoteType getVote(){
		return this.vote;
	}
	
	public void setVote(VoteType vote){
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
