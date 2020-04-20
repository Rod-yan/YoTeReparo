package com.yotereparo.controller.dto.converter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yotereparo.controller.dto.QuoteDto;
import com.yotereparo.model.Address;
import com.yotereparo.model.Quote;
import com.yotereparo.model.User;
import com.yotereparo.service.ServiceManager;
import com.yotereparo.service.UserService;

/**
 * Conversor Entidad -> DTO (y viceversa) para Presupuestos.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Component
public class QuoteConverter implements Converter<Quote, QuoteDto> {
	
	@Autowired
    ModelMapper modelMapper;
	@Autowired
	UserService userService;
	@Autowired
	ServiceManager serviceManager;
	
	@Override
	public QuoteDto convertToDto(Quote quote) {
		QuoteDto quoteDto = modelMapper.map(quote, QuoteDto.class);
		quoteDto.setPrecioTotal(quote.getServicio().getPrecioMinimo(), quote.getServicio().getPrecioInsumos(), quote.getServicio().getPrecioAdicionales());
	    return quoteDto;
	}
	@Override
	public Quote convertToEntity(QuoteDto quoteDto) {
		Quote quote = modelMapper.map(quoteDto, Quote.class);
		quote.setServicio(serviceManager.getServiceById(quoteDto.getServicio()));
		
		User user = userService.getUserById(quoteDto.getUsuarioFinal());
		quote.setUsuarioFinal(user);
		if (quoteDto.getDireccionUsuarioFinal() != null)
			for (Address address : user.getDirecciones()) {
				if (address.equals(quoteDto.getDireccionUsuarioFinal())) {
					quote.setDireccionUsuarioFinal(address);
					break;
				}
			}
	    return quote;
	}
}
