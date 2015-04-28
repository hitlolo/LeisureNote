package com.lolo.dao;

import org.json.JSONArray;

import com.lolo.entity.User;

public interface UserDao {
	public User Login(String username,String password);
	public boolean Register(User user);
	public User RegisterToUser(User user);
	public boolean isOccuipiedUsername(String str);
	public boolean isOccuipiedNickname(String str);
	public JSONArray getUsersBooks(String username);
	
	public Long getLogtime(int userid);
}
