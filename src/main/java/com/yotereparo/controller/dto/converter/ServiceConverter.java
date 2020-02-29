package com.yotereparo.controller.dto.converter;

import java.util.Locale;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yotereparo.controller.dto.ServiceDto;
import com.yotereparo.model.PaymentMethod;
import com.yotereparo.model.Requirement;
import com.yotereparo.model.Service;
import com.yotereparo.service.PaymentMethodService;
import com.yotereparo.service.RequirementService;
import com.yotereparo.service.ServiceTypeService;
import com.yotereparo.service.UserService;
import com.yotereparo.util.error.CustomResponseError;

@Component
public class ServiceConverter extends Converter {
	@Autowired
	UserService userService;
	@Autowired
	ServiceTypeService serviceTypeService;
	@Autowired
	PaymentMethodService paymentMethodService;
	@Autowired
	RequirementService requirementService;
	
	public ServiceDto convertToDto(Service service) {
		ServiceDto serviceDto = modelMapper.map(service, ServiceDto.class);
	    return serviceDto;
	}
	
	public Service convertToEntity(ServiceDto serviceDto) {
		Service service = modelMapper.map(serviceDto, Service.class);
	    if (userService.exist(serviceDto.getUsuarioPrestador()))
	    	service.setUsuarioPrestador(userService.getUserById(serviceDto.getUsuarioPrestador()));
	    else
	    	throw new CustomResponseError("User","usuarioPrestador",messageSource.getMessage("user.doesnt.exist", null, Locale.getDefault()));
	    
	    if (serviceTypeService.exist(serviceDto.getTipoServicio()))
		    service.setTipoServicio(serviceTypeService.getServiceTypeByDescription(serviceDto.getTipoServicio()));
		else
			throw new CustomResponseError("ServiceType","tipoServicio",messageSource.getMessage("serviceType.doesnt.exist", null, Locale.getDefault()));
	    
	    service.getMediosDePago().forEach((Consumer<? super PaymentMethod>) pm -> {
	    	if (!paymentMethodService.exist(pm.getId()))
	    		throw new CustomResponseError("PaymentMethod","mediosDePago",messageSource.getMessage("paymentMethod.doesnt.exist", null, Locale.getDefault()));
	    });
	    
	    service.getRequerimientos().forEach((Consumer<? super Requirement>) rq -> {
	    	if (!requirementService.exist(rq.getId()))
	    		throw new CustomResponseError("Requirement","requerimientos",messageSource.getMessage("requirement.doesnt.exist", null, Locale.getDefault()));
	    });
	    return service;
	}
}
