package com.yotereparo.controller.mapping;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yotereparo.controller.dto.QuoteDto;
import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.model.Contract;
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
public class ServiceMapper implements Mapper<Service, ServiceDto> {
	
	@Autowired
    ModelMapper modelMapper;
	@Autowired
	UserService userService;
	@Autowired
    QuoteService quoteService;
	@Autowired
	ServiceTypeService serviceTypeService;
	@Autowired 
	QuoteMapper quoteConverter;
	
	@Override
	public ServiceDto convertToDto(Service service) {
		ServiceDto serviceDto = modelMapper.map(service, ServiceDto.class);
		// Hacemos pasar cada Presupuesto por su respectivo converter
		// para no omitir cualquier regla que se aplique en el mismo.
		Set<Quote> quotes = service.getPresupuestos();
	    if (quotes != null && !quotes.isEmpty()) {
	    	serviceDto.setPresupuestos(new HashSet<QuoteDto>(0));
	    	int ratedContracts = 0;
			int accumulatedRating = 0;
	    	for (Quote quote : quotes) {
	    		serviceDto.addPresupuesto(quoteConverter.convertToDto(quote));
	    		// Promediamos la valoración del servicio con las valoraciones de cada uno de sus contratos.
	    		Contract contract = quote.getContrato();
				if (contract != null && contract.getValoracion() != null) {
					ratedContracts++;
					accumulatedRating = accumulatedRating + contract.getValoracion();
				}
	    	}
	    	if (ratedContracts != 0) {
				serviceDto.setValoracionPromedio( 
						(float) (Math.round(((float) accumulatedRating / (float) ratedContracts) * 10.0)/10.0));
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
