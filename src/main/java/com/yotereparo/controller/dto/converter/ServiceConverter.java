package com.yotereparo.controller.dto.converter;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.model.Service;
import com.yotereparo.service.ServiceTypeService;
import com.yotereparo.service.UserService;
import com.yotereparo.util.error.CustomResponseError;

@Component
public class ServiceConverter extends Converter {
	@Autowired
	UserService userService;
	@Autowired
	ServiceTypeService serviceTypeService;
	
	public ServiceDto convertToDto(Service service) {
		ServiceDto serviceDto = modelMapper.map(service, ServiceDto.class);
	    return serviceDto;
	}
	
	public Service convertToEntity(ServiceDto serviceDto) {
		Service service = modelMapper.map(serviceDto, Service.class);
		if (serviceTypeService.exist(serviceDto.getTipoServicio()))
		    service.setTipoServicio(serviceTypeService.getServiceTypeByDescription(serviceDto.getTipoServicio()));
		else
			throw new CustomResponseError("ServiceType","tipoServicio",messageSource.getMessage("serviceType.doesnt.exist", null, Locale.getDefault()));
		    
	    if (userService.exist(serviceDto.getUsuarioPrestador()))
	    	service.setUsuarioPrestador(userService.getUserById(serviceDto.getUsuarioPrestador()));
	    else
	    	throw new CustomResponseError("User","usuarioPrestador",messageSource.getMessage("user.doesnt.exist", null, Locale.getDefault()));
	    return service;
	}
}
