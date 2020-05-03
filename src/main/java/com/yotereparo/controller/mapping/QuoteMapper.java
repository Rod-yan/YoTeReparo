package com.yotereparo.controller.mapping;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yotereparo.controller.dto.QuoteDto;
import com.yotereparo.model.Address;
import com.yotereparo.model.Contract;
import com.yotereparo.model.Quote;
import com.yotereparo.model.User;
import com.yotereparo.service.ContractService;
import com.yotereparo.service.ServiceManager;
import com.yotereparo.service.UserService;

/**
 * Conversor Entidad -> DTO (y viceversa) para Presupuestos.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Component
public class QuoteMapper implements Mapper<Quote, QuoteDto> {
	
	@Autowired
    ModelMapper modelMapper;
	@Autowired
	UserService userService;
	@Autowired
	ServiceManager serviceManager;
	@Autowired
	ContractService contractService;
	
	@Override
	public QuoteDto convertToDto(Quote quote) {
		QuoteDto quoteDto = modelMapper.map(quote, QuoteDto.class);
		quoteDto.setPrecioTotal(quote.getServicio().getPrecioMinimo(), quote.getServicio().getPrecioInsumos(), quote.getServicio().getPrecioAdicionales());
		
		Contract contract = quote.getContrato();
		if (contract != null)
			quoteDto.setContrato(contract.getId());
	    return quoteDto;
	}
	
	@Override
	public Quote convertToEntity(QuoteDto quoteDto) {
		Quote quote = modelMapper.map(quoteDto, Quote.class);
		quote.setServicio(serviceManager.getServiceById(quoteDto.getServicio()));
		
		Integer contractId = quoteDto.getContrato();
		if (contractId != null)
			quote.setContrato(contractService.getContractById(contractId));
		
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
