package com.yotereparo.controller.dto.converter;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.controller.dto.UserDto;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;
import com.yotereparo.service.CityService;
import com.yotereparo.service.ServiceManager;
import com.yotereparo.util.error.CustomResponseError;

@Component
public class UserConverter extends Converter {
	@Autowired
    CityService cityService;
	@Autowired
    ServiceManager serviceManager;
	@Autowired 
	ServiceConverter serviceConverter;
	
	public UserDto convertToDto(User user) {
	    UserDto userDto = modelMapper.map(user, UserDto.class);
	    Set<Service> services = user.getServicios();
	    if (services != null && !services.isEmpty()) {
	    	userDto.setServicios(new HashSet<ServiceDto>(0));
	    	for (Service serv : services) {
	    		userDto.addServicio(serviceConverter.convertToDto(serv));
	    	}
	    }
	    return userDto;
	}
	
	public User convertToEntity(UserDto userDto) {
	    User user = modelMapper.map(userDto, User.class);
	    if (cityService.exist(userDto.getCiudad()))
	    	user.setCiudad(cityService.getCityById(userDto.getCiudad()));
	    else
	    	throw new CustomResponseError("City","ciudad",messageSource.getMessage("city.doesnt.exist", null, Locale.getDefault()));
	    Set<ServiceDto> servicesDto = userDto.getServicios();
	    if (servicesDto != null && !servicesDto.isEmpty()) {
	    	user.setServicios(new HashSet<Service>(0));
	    	for (ServiceDto servId : servicesDto) {
	    		if (serviceManager.exist(servId.getId()))
	    			user.addServicio(serviceManager.getServiceById(servId.getId()));
	    		else
	    			throw new CustomResponseError("Service","servicios",messageSource.getMessage("service.doesnt.exist", new Integer[]{servId.getId()}, Locale.getDefault()));
	    	}
	    }
	    return user;
	}
}