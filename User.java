package com.common.annotation;

import java.sql.Timestamp;

@TableName("tbl_user")
public class User {
	@Column("age")
	private int id;
	
	@Column("name")
	private String name;
	
	@Column("age")
	private int userAge;
	
	@Column("born")
	private Timestamp bornDate;
	
	@Column("email")
	private String email;
	
	@Column("qq")
	private String qq;
	
	
	public int getId() {return id;}  
    public void setId(int id) {this.id = id;}  
    public String getName() {return name;}  
    public void setName(String name) {this.name = name;}  
    public int getUserAge() {return userAge;}  
    public void setUserAge(int userAge) {this.userAge = userAge;}
    public Timestamp getBornDate(){return bornDate;}
    public void setBornDate(Timestamp bornDate){this.bornDate = bornDate;}
    public String getEmail() {return email;}  
    public void setEmail(String email) {this.email = email;}
    public String getQq() {return qq;}  
    public void setQq(String qq) {this.qq = qq;}
}
