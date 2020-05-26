package com.yotereparo.controller.mapping;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.controller.dto.UserDto;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;
import com.yotereparo.service.CityService;

/**
 * Conversor Entidad -> DTO (y viceversa) para Usuarios.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Component
public class UserMapper implements Mapper<User, UserDto> {
	
	@Autowired
    ModelMapper modelMapper;
	@Autowired
    CityService cityService;
	@Autowired 
	ServiceMapper serviceMapper;
	@Autowired 
	MessageMapper messageMapper;
	
	@Override
	public UserDto convertToDto(User user) {
	    UserDto userDto = modelMapper.map(user, UserDto.class);
	    // Hacemos pasar cada Servicio por su respectivo converter para no omitir cualquier regla que se aplique en el mismo.
	    Set<Service> services = user.getServicios();
	    if (services != null && !services.isEmpty()) {
	    	userDto.setServicios(new HashSet<ServiceDto>(0));
	    	for (Service serv : services) {
	    		userDto.addServicio(serviceMapper.convertToDto(serv));
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
	    return user;
	}
}
