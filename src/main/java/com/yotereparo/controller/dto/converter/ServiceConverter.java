package com.yotereparo.controller.dto.converter;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.model.Service;
import com.yotereparo.service.UserService;
import com.yotereparo.util.error.CustomResponseError;

@Component
public class ServiceConverter extends Converter {
	@Autowired
	UserService userService;
	
	@SuppressWarnings("unused")
	public ServiceDto convertToDto(Service service) {
		ServiceDto serviceDto = modelMapper.map(service, ServiceDto.class);
	    return serviceDto;
	}
	
	@SuppressWarnings("unused")
	public Service convertToEntity(ServiceDto servideDto) {
		Service service = modelMapper.map(servideDto, Service.class);
	    if (userService.exist(servideDto.getUsuarioPrestador()))
	    	service.setUsuarioPrestador(userService.getUserById(servideDto.getUsuarioPrestador()));
	    else
	    	throw new CustomResponseError("User","usuarioPrestador",messageSource.getMessage("user.doesnt.exist", null, Locale.getDefault()));
	    return service;
	}
}
