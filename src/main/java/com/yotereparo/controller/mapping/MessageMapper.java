package com.yotereparo.controller.mapping;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yotereparo.controller.dto.MessageDto;
import com.yotereparo.model.Message;
import com.yotereparo.service.ServiceManager;
import com.yotereparo.service.UserService;

/**
 * Conversor Entidad -> DTO (y viceversa) para Mensajes.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Component
public class MessageMapper implements Mapper<Message, MessageDto> {
	
	@Autowired
    ModelMapper modelMapper;
	@Autowired
	UserService userService;
	@Autowired
	ServiceManager serviceManager;
	
	@Override
	public MessageDto convertToDto(Message message) {
	    return modelMapper.map(message, MessageDto.class);
	}
	
	@Override
	public Message convertToEntity(MessageDto messageDto) {
		Message message = modelMapper.map(messageDto, Message.class);
		message.setServicio(serviceManager.getServiceById(messageDto.getServicio()));
		message.setUsuarioFinal(userService.getUserById(messageDto.getUsuarioFinal()));
	    return message;
	}
}
