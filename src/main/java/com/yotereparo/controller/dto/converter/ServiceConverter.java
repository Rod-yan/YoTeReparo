package com.yotereparo.controller.dto.converter;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yotereparo.controller.dto.QuoteDto;
import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.model.Quote;
import com.yotereparo.model.Service;
import com.yotereparo.service.QuoteService;
import com.yotereparo.service.ServiceTypeService;
import com.yotereparo.service.UserService;

/**
 * Conversor Entidad -> DTO (y viceversa) para Servicios.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Component
public class ServiceConverter implements Converter<Service, ServiceDto> {
	
	@Autowired
    ModelMapper modelMapper;
	@Autowired
	UserService userService;
	@Autowired
    QuoteService quoteService;
	@Autowired
	ServiceTypeService serviceTypeService;
	@Autowired 
	QuoteConverter quoteConverter;
	
	@Override
	public ServiceDto convertToDto(Service service) {
		ServiceDto serviceDto = modelMapper.map(service, ServiceDto.class);
		// Hacemos pasar cada Presupuesto por su respectivo converter para no omitir cualquier regla que se aplique en el mismo.
		Set<Quote> quotes = service.getPresupuestos();
	    if (quotes != null && !quotes.isEmpty()) {
	    	serviceDto.setPresupuestos(new HashSet<QuoteDto>(0));
	    	for (Quote qte : quotes) {
	    		serviceDto.addPresupuesto(quoteConverter.convertToDto(qte));
	    	}
	    }
	    return serviceDto;
	}
	@Override
	public Service convertToEntity(ServiceDto serviceDto) {
		Service service = modelMapper.map(serviceDto, Service.class);
		service.setUsuarioPrestador(userService.getUserById(serviceDto.getUsuarioPrestador().toLowerCase()));
		service.setTipoServicio(serviceTypeService.getServiceTypeByDescription(serviceDto.getTipoServicio()));
		// Obtenemos cada Presupuesto desde la persistencia para no omitir reglas de construcción en el modelo
		Set<QuoteDto> quotesDto = serviceDto.getPresupuestos();
	    if (quotesDto != null && !quotesDto.isEmpty()) {
	    	service.setPresupuestos(new HashSet<Quote>(0));
	    	for (QuoteDto qteDto : quotesDto)
	    		service.addPresupuesto(quoteService.getQuoteById(qteDto.getId()));
	    }
	    return service;
	}
}
