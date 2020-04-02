package com.yotereparo.controller.dto.converter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yotereparo.controller.dto.QuoteDto;
import com.yotereparo.model.Quote;
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
		quote.setUsuarioFinal(userService.getUserById(quoteDto.getUsuarioFinal().toLowerCase()));
		quote.setServicio(serviceManager.getServiceById(quoteDto.getServicio()));
	    return quote;
	}
}
