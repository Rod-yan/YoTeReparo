package com.yotereparo.controller.mapping;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yotereparo.controller.dto.MessageDto;
import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.model.Contract;
import com.yotereparo.model.Message;
import com.yotereparo.model.Quote;
import com.yotereparo.model.Service;
import com.yotereparo.model.ServiceRatingEntry;
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
	ServiceTypeService serviceTypeService;
	@Autowired 
	MessageMapper messageMapper;
	
	@Override
	public ServiceDto convertToDto(Service service) {
		ServiceDto serviceDto = modelMapper.map(service, ServiceDto.class);
		// Cálculo de la valoración del servicio
		Set<Quote> quotes = service.getPresupuestos();
	    if (quotes != null && !quotes.isEmpty()) {
	    	int ratedContracts = 0;
			int accumulatedRating = 0;
	    	for (Quote quote : quotes) {
	    		Contract contract = quote.getContrato();
				if (contract != null && contract.getValoracion() != null) {
					// Agregamos la valoración al DTO de servicio.
					serviceDto.addValoracion(
							new ServiceRatingEntry(contract.getValoracion(), contract.getDescripcionValoracion()));
					// Promediamos la valoración del servicio con las valoraciones de cada uno de sus contratos.
					ratedContracts++;
					accumulatedRating = accumulatedRating + contract.getValoracion();
				}
	    	}
	    	if (ratedContracts != 0) {
				serviceDto.setValoracionPromedio( 
						(float) (Math.round(((float) accumulatedRating / (float) ratedContracts) * 10.0)/10.0));
	    	}
	    }
	    // Hacemos pasar cada Mensaje por su respectivo converter para no omitir cualquier regla que se aplique en el mismo.
	    Set<Message> mensajes = service.getMensajes();
	    if (mensajes != null && !mensajes.isEmpty()) {
	    	serviceDto.setMensajes(new HashSet<MessageDto>(0));
	    	for (Message msg : mensajes) {
	    		serviceDto.addMensaje(messageMapper.convertToDto(msg));
	    	}
	    }
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
