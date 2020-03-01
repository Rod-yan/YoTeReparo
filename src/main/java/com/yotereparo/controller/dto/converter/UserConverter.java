package com.yotereparo.controller.dto.converter;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.controller.dto.UserDto;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;
import com.yotereparo.service.CityService;
import com.yotereparo.service.ServiceManager;

@Component
public class UserConverter implements Converter<User, UserDto> {
	
	@Autowired
    ModelMapper modelMapper;
	@Autowired
    MessageSource messageSource;
	@Autowired
    CityService cityService;
	@Autowired
    ServiceManager serviceManager;
	@Autowired 
	ServiceConverter serviceConverter;
	
	@Override
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
	@Override
	public User convertToEntity(UserDto userDto) {
		// Quito ID a direcciones ya que estos son autogenerados
		userDto.getDirecciones().forEach(direccion -> direccion.setId(null));
		
	    User user = modelMapper.map(userDto, User.class);
	    user.setCiudad(cityService.getCityById(userDto.getCiudad()));
	    
	    Set<ServiceDto> servicesDto = userDto.getServicios();
	    if (servicesDto != null && !servicesDto.isEmpty()) {
	    	user.setServicios(new HashSet<Service>(0));
	    	for (ServiceDto servId : servicesDto)
	    		user.addServicio(serviceManager.getServiceById(servId.getId()));
	    }
	    return user;
	}
}
