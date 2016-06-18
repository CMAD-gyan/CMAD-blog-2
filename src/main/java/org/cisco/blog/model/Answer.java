package org.cisco.blog.model;

import java.sql.Timestamp;
import java.util.List;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.utils.IndexType;
import org.bson.types.ObjectId;
import org.cisco.blog.model.VoteType;


@Entity
@Indexes({
	@Index(fields = @Field(value = "$**", type = IndexType.TEXT))
})
public class Answer {
	@Id
	private ObjectId id;

	private String text;

	private Timestamp createTime;
	private Timestamp updateTime;

	private String userName;
	@Reference
	private User user;

	@Embedded
	private List<Vote> votes;
	private int totalVotes;
	@Embedded
	private List<Comment>  comments;


	public Answer() {

	}

	public Answer(String text, String userName, User user) {
		this.text  = text;
		//@toDo	
		this.user  = user;
		this.userName  = userName;		
		this.createTime  = new Timestamp(System.currentTimeMillis());
		this.updateTime  = new Timestamp(System.currentTimeMillis());
		this.votes       = null;
		this.comments    = null;
		this.totalVotes    =  0;
	}

	public String getId(){
		return this.id.toHexString();
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


	public void setUpdateTime(){
		this.updateTime = new Timestamp(System.currentTimeMillis());
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

	public User getUser(){
		return this.user;
	}

	public void setUser(User user){
		this.user = user;
	}


	public int getTotalVotes(){
		return this.totalVotes;
	}

	public void setTotalVotes(int totalVotes){
		this.totalVotes = totalVotes;
	}

	public List< Vote > getVotes() { return votes; }
	public void setVotes( List< Vote > votes ) { this.votes = votes; }

	public List< Comment > getComments() { return comments; }
	public void setComments( List< Comment > comments ) { this.comments = comments; }


}
