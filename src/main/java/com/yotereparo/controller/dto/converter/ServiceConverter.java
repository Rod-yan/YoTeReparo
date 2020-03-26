package com.yotereparo.controller.dto.converter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.model.Service;
import com.yotereparo.service.ServiceTypeService;
import com.yotereparo.service.UserService;

@Component
public class ServiceConverter implements Converter<Service, ServiceDto> {
	
	@Autowired
    ModelMapper modelMapper;
	@Autowired
	UserService userService;
	@Autowired
	ServiceTypeService serviceTypeService;
	
	@Override
	public ServiceDto convertToDto(Service service) {
		ServiceDto serviceDto = modelMapper.map(service, ServiceDto.class);
	    return serviceDto;
	}
	@Override
	public Service convertToEntity(ServiceDto serviceDto) {
		Service service = modelMapper.map(serviceDto, Service.class);
		service.setUsuarioPrestador(userService.getUserById(serviceDto.getUsuarioPrestador().toLowerCase()));
		service.setTipoServicio(serviceTypeService.getServiceTypeByDescription(serviceDto.getTipoServicio()));
	    return service;
	}
}
