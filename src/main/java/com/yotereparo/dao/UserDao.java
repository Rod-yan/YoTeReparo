package com.yotereparo.dao;

import java.util.List;

import com.yotereparo.model.User;

public interface UserDao {
	
	User getUserById(String id);
	
	User getUserByEmail(String email);
	 
    void createUser(User user);
     
    void deleteUserById(String id);
     
    List<User> getAllUsers();
}
