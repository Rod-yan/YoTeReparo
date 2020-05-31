package com.yotereparo.dao;

import java.util.List;

import com.yotereparo.model.Message;

public interface MessageDao {
	
	Message getMessageById(Integer id);
	 
    void createMessage(Message message);
     
    void deleteMessageById(Integer id);
     
    List<Message> getAllMessages();
}
