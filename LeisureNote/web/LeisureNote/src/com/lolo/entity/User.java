package com.lolo.entity;

/*
 * Author:Lolo
 * Date:2012/3/14
 * Class Name:User
 * Class Description:Receive the data of user as an object.
 */

public class User 
{
	
	private int    id;
	private String username;
	private String password;
	private String name;
	private String gender;
	private String headimg_url;
	
	//need modify
	
	private String remark;

	public int getId() 
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getUsername() 
	{
		return username;
	}

	public void setUsername(String username) 
	{
		this.username = username;
	}

	public String getPassword() 
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getGender() 
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	public String getHeadimg_url() 
	{
		return headimg_url;
	}

	public void setHeadimg_url(String headimg_url)
	{
		this.headimg_url = headimg_url;
	}

	public String getRemark() 
	{
		return remark;
	}

	public void setRemark(String remark) 
	{
		this.remark = remark;
	}


	
	
}
