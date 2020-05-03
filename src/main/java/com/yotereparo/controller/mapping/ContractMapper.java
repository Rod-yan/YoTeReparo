package com.yotereparo.controller.mapping;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yotereparo.controller.dto.ContractDto;
import com.yotereparo.model.Contract;
import com.yotereparo.service.QuoteService;

/**
 * Conversor Entidad -> DTO (y viceversa) para Contratos.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Component
public class ContractMapper implements Mapper<Contract, ContractDto> {
	
	@Autowired
    ModelMapper modelMapper;
	@Autowired
	QuoteService quoteService;
	
	@Override
	public ContractDto convertToDto(Contract contract) {
	    return modelMapper.map(contract, ContractDto.class);
	}
	
	@Override
	public Contract convertToEntity(ContractDto contractDto) {
		Contract contract = modelMapper.map(contractDto, Contract.class);
		contract.setPresupuesto(quoteService.getQuoteById(contractDto.getPresupuesto()));
	    return contract;
	}
}
