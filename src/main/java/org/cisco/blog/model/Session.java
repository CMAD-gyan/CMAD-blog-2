package org.cisco.blog.model;


import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.utils.IndexDirection;


@Entity
public class Session {
	@Id
	@Indexed(value=IndexDirection.ASC, name="id", unique=true)
	private ObjectId id;
	@Indexed(value=IndexDirection.ASC, name="userid", unique=true)
	private String userId;
	
	//Sessio expires after one hour
	@Indexed(name="user_last_login_time_indx", expireAfterSeconds = 60*60*1)
	private Date lastLoginTime;
	
	public Session() {
	}
	
	public String getId(){
		return this.id.toHexString();
	}
	
	public String getUserId(){
		return this.userId;
	}
	
	public void setUserId( String userId){
		this.userId = userId;
	}
	
	public Date getLastLoginTime(){
		return this.lastLoginTime;
	}
	
	public void setLastLoginTime( Date lastLoginTime){
		this.lastLoginTime = lastLoginTime;
	}
}
