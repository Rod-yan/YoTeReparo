package com.yotereparo.service;

import java.util.List;

import com.yotereparo.model.User;

public interface UserService {
    
    void createUser(User user);
     
    void updateUser(User user);
     
    void deleteUserById(String id);
 
    List<User> getAllUsers(); 
     
    User getUserById(String id);
    
    void updateUserPhotoById(String id, byte[] b64photo);
    
    boolean exist(String id);
 
    boolean hasUniqueId(String id);
    
    boolean userIsPrestador(User user);
    
    boolean userIsFinal(User user);
}
