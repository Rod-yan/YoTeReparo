package com.yotereparo.service;

import java.util.List;

import com.yotereparo.model.Message;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;

public interface MessageService {
    
    void createMessage(Message message);
    
    void refreshMessageStatus(Message message);
     
    void updateMessage(Message message);
    
    void deleteMessageById(Integer id);
     
    Message getMessageById(Integer id);
    
    List<Message> getAllMessages();
    
    boolean wasServiceRecentlyMessagedByUser(Service service, User user);
}
