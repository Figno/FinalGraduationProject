package com.chxip.localmusic.entity;

import java.io.Serializable;

//用户类
public class User implements Serializable{
	private static final long serialVersionUID = -931300867759695818L;
	private int id;
	private String name;
	private String password;
	private int type;
	private String realName;
	private String imageUrl;
    private String phone;
    private String sex;
    private String email;
    private int state;//1 启用，2 停用
    
	private boolean isUpdate;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	

}
