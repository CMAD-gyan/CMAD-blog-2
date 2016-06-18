package org.cisco.blog.model;

import java.sql.Timestamp;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.utils.IndexDirection;

@Entity
public class User {
	@Id
	private ObjectId id;

	@Indexed(value=IndexDirection.ASC, name="username", unique=true)
	private String username;
	private String password;
	private String name;

	@Indexed(value=IndexDirection.ASC, name="email", unique=true)
	private String email;
	private int score;
	private Timestamp createTime;
	private Timestamp updateTime;

	User() {
	}

	public String getId(){
		return this.id.toHexString();
	}

	public String getUsername(){
		return this.username;
	}

	public void setUsername( String username){
		this.username = username;
	}

	public String getPassword(){
		return this.password;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public String getName(){
		return this.name;
	}

	public void setName( String name){
		this.name = name;
	}

	public String getEmail(){
		return this.email;
	}

	public void setEmail( String email){
		this.email = email;
	}

	public boolean isValidEmailAddress(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
	}

	public int getScore(){
		return this.score;
	}

	public void setScore( int score){
		this.score = score;
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

}
