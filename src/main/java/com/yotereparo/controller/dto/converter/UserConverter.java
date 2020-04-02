package com.yotereparo.controller.dto.converter;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yotereparo.controller.dto.QuoteDto;
import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.controller.dto.UserDto;
import com.yotereparo.model.Quote;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;
import com.yotereparo.service.CityService;
import com.yotereparo.service.QuoteService;
import com.yotereparo.service.ServiceManager;

/**
 * Conversor Entidad -> DTO (y viceversa) para Usuarios.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Component
public class UserConverter implements Converter<User, UserDto> {
	
	@Autowired
    ModelMapper modelMapper;
	@Autowired
    CityService cityService;
	@Autowired
    ServiceManager serviceManager;
	@Autowired
    QuoteService quoteService;
	@Autowired 
	ServiceConverter serviceConverter;
	@Autowired 
	QuoteConverter quoteConverter;
	
	@Override
	public UserDto convertToDto(User user) {
	    UserDto userDto = modelMapper.map(user, UserDto.class);
	    // Hacemos pasar cada Servicio por su respectivo converter para no omitir cualquier regla que se aplique en el mismo.
	    Set<Service> services = user.getServicios();
	    if (services != null && !services.isEmpty()) {
	    	userDto.setServicios(new HashSet<ServiceDto>(0));
	    	for (Service serv : services) {
	    		userDto.addServicio(serviceConverter.convertToDto(serv));
	    	}
	    }
	    // Hacemos pasar cada Presupuesto por su respectivo converter para no omitir cualquier regla que se aplique en el mismo.
	    Set<Quote> quotes = user.getPresupuestos();
	    if (quotes != null && !quotes.isEmpty()) {
	    	userDto.setPresupuestos(new HashSet<QuoteDto>(0));
	    	for (Quote qte : quotes) {
	    		userDto.addPresupuesto(quoteConverter.convertToDto(qte));
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
	    // Obtenemos cada Servicio desde la persistencia para no omitir reglas de construcción en el modelo
	    Set<ServiceDto> servicesDto = userDto.getServicios();
	    if (servicesDto != null && !servicesDto.isEmpty()) {
	    	user.setServicios(new HashSet<Service>(0));
	    	for (ServiceDto servDto : servicesDto)
	    		user.addServicio(serviceManager.getServiceById(servDto.getId()));
	    }
	    // Obtenemos cada Presupuesto desde la persistencia para no omitir reglas de construcción en el modelo
	    Set<QuoteDto> quotesDto = userDto.getPresupuestos();
	    if (quotesDto != null && !quotesDto.isEmpty()) {
	    	user.setPresupuestos(new HashSet<Quote>(0));
	    	for (QuoteDto qteDto : quotesDto)
	    		user.addPresupuesto(quoteService.getQuoteById(qteDto.getId()));
	    }
	    return user;
	}
}
