package com.yotereparo.service;

import java.util.List;

import com.yotereparo.model.User;

public interface UserService {
    
    void createUser(User user);
     
    void updateUser(User user);
    
    void changeUserPasswordById(String id, String currentPassword, String newPassword);
    
    void registerSuccessfulLoginAttempt(User user);

    void registerFailedLoginAttempt(User user);
    	
    void deleteUserById(String id);
 
    List<User> getAllUsers(); 
     
    User getUserById(String id);
    
    void updateUserPhotoById(String id, byte[] b64photo);
    
    boolean isProvider(User user);
    
    boolean isCustomer(User user);
}
