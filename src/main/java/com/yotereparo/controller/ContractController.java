package com.yotereparo.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yotereparo.controller.dto.ContractDto;
import com.yotereparo.controller.dto.validation.ContractValidation;
import com.yotereparo.controller.mapping.ContractMapper;
import com.yotereparo.model.Contract;
import com.yotereparo.model.Quote;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;
import com.yotereparo.service.ContractService;
import com.yotereparo.service.UserService;
import com.yotereparo.util.MiscUtils;
import com.yotereparo.util.error.CustomResponseError;
/**
 * Controlador REST SpringMVC que expone servicios básicos para la gestión de Contratos.
 * 
 * @author Rodrigo Yanis
 * 
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class ContractController {
	
	private static final Logger logger = LoggerFactory.getLogger(ContractController.class);
	
	@Autowired
    ContractService contractService;
	@Autowired
    UserService userService;
	@Autowired
    MessageSource messageSource;
	@Autowired
	ContractValidation contractValidation;
	@Autowired
	ContractMapper contractMapper;
	@Autowired
	MiscUtils miscUtils;

	/*
	 * Devuelve todos los Contratos registradas en formato JSON.
	 */
	@RequestMapping(
			value = { "/contracts" }, 
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('USUARIO_FINAL')")
	public ResponseEntity<?> listContracts(@RequestParam(required = false) String userRole) {
		logger.info("ListContracts - GET - Processing request for a list with all existing contracts.");
        try {
        	Set<Contract> contracts = new HashSet<Contract>(0);
        	
        	String authenticatedUsername = 
					((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
			User authenticatedUser = userService.getUserById(authenticatedUsername);
			
			if (userRole == null || userRole.isEmpty()) {
				if (userService.isServiceAccountOrAdministrator(authenticatedUser))
					contracts = new HashSet<Contract>(contractService.getAllContracts());
				else {
					logger.warn("ListContracts - GET - Request failed - User <{}> doesn't have access to all contracts.", 
							authenticatedUsername);
					FieldError error = new FieldError("Authorization","error",
							messageSource.getMessage("client.error.unauthorized", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
				}
			}
			else if ("customer".equalsIgnoreCase(userRole)) {
				logger.debug("Fetching all contracts of customer: <{}>", authenticatedUsername);
				for(Quote quote : authenticatedUser.getPresupuestos()) {
					Contract contract = quote.getContrato();
					if (contract != null) {
						contractService.refreshContractStatus(contract);
						contracts.add(contract);
					}
				}
			}
			else if ("provider".equalsIgnoreCase(userRole)) {
				logger.debug("Fetching all contracts of provider: <{}>", authenticatedUsername);
				for (Service service : authenticatedUser.getServicios())
					for(Quote quote : service.getPresupuestos()) {
						Contract contract = quote.getContrato();
						if (contract != null) {
							contractService.refreshContractStatus(contract);
							contracts.add(contract);
						}
					}
			}
            
        	if (contracts != null && !contracts.isEmpty()) {
				
				List<ContractDto> contractsDto = contracts.stream()
		                .map(contract -> contractMapper.convertToDto(contract))
		                .collect(Collectors.toList());
				
	        	logger.info("ListContracts - GET - Exiting method, providing response resource to client.");
	            return new ResponseEntity<List<ContractDto>>(contractsDto, HttpStatus.OK);
            }
            else {
            	logger.warn("ListContracts - GET - Request failed - No contracts were found.");
            	return new ResponseEntity<List<ContractDto>>(HttpStatus.NO_CONTENT);
            }
        }
		catch (Exception e) {
			logger.error("ListContracts - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Contract","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}   
    }
	
	/*
	 * Devuelve el Contrato solicitado en formato JSON.
	 */
	@RequestMapping(
			value = { "/contracts/{id}" }, 
			produces = "application/json; charset=UTF-8", 
			method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('USUARIO_FINAL')")
	public ResponseEntity<?> getContract(@PathVariable("id") Integer id) {
		logger.info("GetContract - GET - Processing request for contract <{}>.", id);
        try {
        	Contract contract = contractService.getContractById(id);
            
    		if (contract != null) {
    			/* 
    			 * Validamos si el contrato siendo procesado le pertenezca 
    			 * al usuario autenticado (como usuario prestador, o como usuario final)
    			 */
    			String authenticatedUsername = 
    					((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    			boolean isServiceAccountOrAdministrator = 
    					userService.isServiceAccountOrAdministrator(userService.getUserById(authenticatedUsername));
    			boolean isOwnerAndCustomer = 
    					contract.getPresupuesto().getUsuarioFinal().getId().equalsIgnoreCase(authenticatedUsername);
    			boolean isOwnerAndProvider = 
    					contract.getPresupuesto().getServicio().getUsuarioPrestador().getId().equalsIgnoreCase(authenticatedUsername);
    					
    			if (isServiceAccountOrAdministrator || isOwnerAndCustomer || isOwnerAndProvider) {
            		logger.info("GetContract - GET - Exiting method, providing response resource to client.");
                    return new ResponseEntity<ContractDto>(contractMapper.convertToDto(contract), HttpStatus.OK);
            	}
    			else {
    				logger.warn("GetContract - GET - Request failed - User <{}> doesn't have access to contract <{}> in this context.", 
    						authenticatedUsername, id);
    				FieldError error = new FieldError("Authorization","error",
    						messageSource.getMessage("client.error.unauthorized", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
    			}
            }
            else {
            	logger.warn("GetContract - GET - Request failed - Contract with id <{}> not found.", id);
                FieldError error = new FieldError("Contract","error",
                		messageSource.getMessage("contract.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
                return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
            } 
        }
        catch (Exception e) {
			logger.error("GetContract - GET - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Contract","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
	
	/*
	 * Permite la valorización de contratos para usuarios finales.
	 */
	@RequestMapping(
			value = { "/contracts/{id}" }, 
			consumes = "application/json; charset=UTF-8",
			produces = "application/json; charset=UTF-8",
			method = RequestMethod.PUT)
	@PreAuthorize("hasAuthority('USUARIO_FINAL')")
    public ResponseEntity<?> rateContract(@PathVariable("id") Integer id, @RequestBody ContractDto clientInput, BindingResult result) {	
		logger.info("RateContract - PUT - Processing request for contract <{}>.", id);
		try {
			Contract contract = contractService.getContractById(id);
			if (contract != null) {
				/* 
    			 * Validamos si el contrato siendo procesado le pertenezca 
    			 * al usuario autenticado (como usuario final)
    			 */
    			String authenticatedUsername = 
    					((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    			boolean isServiceAccountOrAdministrator = 
    					userService.isServiceAccountOrAdministrator(userService.getUserById(authenticatedUsername));
    			boolean isOwnerAndCustomer = 
    					contract.getPresupuesto().getUsuarioFinal().getId().equalsIgnoreCase(authenticatedUsername);
    			
    			if (isServiceAccountOrAdministrator || isOwnerAndCustomer) {
    				if (!contractValidation.validateRequest(clientInput, result).hasErrors()) {
    					contractService.rateContractById(id, clientInput.getValoracion(), clientInput.getDescripcionValoracion());
						
						logger.info("RateContract - PUT - Exiting method, providing response resource to client.");
						return new ResponseEntity<ContractDto>(contractMapper.convertToDto(contractService.getContractById(id)), HttpStatus.OK);
    				}
    				else {
    					logger.warn("RateContract - PUT - Request failed - Input validation error(s) detected.");
    					return new ResponseEntity<>(miscUtils.getFormatedResponseErrorList(result), HttpStatus.BAD_REQUEST);
    				}
    			}
    			else {
    				logger.warn("RateContract - PUT - Request failed - User <{}> doesn't have access to contract <{}> in this context.", 
    						authenticatedUsername, id);
    				FieldError error = new FieldError("Authorization","error",
    						messageSource.getMessage("client.error.unauthorized", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
    			}
	        }
			else {
				logger.warn("RateContract - PUT - Request failed - Unable to update contract. Contract <{}> doesn't exist.", id);
	            FieldError error = new FieldError("Contract","error",
	            		messageSource.getMessage("contract.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	            return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
			}
		}
		catch (CustomResponseError e) {
			logger.warn("RateContract - PUT - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("RateContract - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Contract","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
	
	/*
	 * Confirma la finalización de un contrato por parte del usuario final o usuario prestador.
	 */
	@RequestMapping(
			value = { "/contracts/{id}/finish" }, 
			produces = "application/json; charset=UTF-8",			
			method = RequestMethod.PUT)
	@PreAuthorize("hasAuthority('USUARIO_FINAL')")
    public ResponseEntity<?> finishContract(@PathVariable("id") Integer id) {
		logger.info("FinishContract - PUT - Processing request for contract <{}>.", id);
		try {
			Contract contract = contractService.getContractById(id);
			if (contract != null) {
				/* 
    			 * Validamos si el contrato siendo procesado le pertenezca 
    			 * al usuario autenticado (como usuario prestador, o como usuario final)
    			 */
    			String authenticatedUsername = 
    					((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    			boolean isServiceAccountOrAdministrator = 
    					userService.isServiceAccountOrAdministrator(userService.getUserById(authenticatedUsername));
    			boolean isOwnerAndCustomer = 
    					contract.getPresupuesto().getUsuarioFinal().getId().equalsIgnoreCase(authenticatedUsername);
    			boolean isOwnerAndProvider = 
    					contract.getPresupuesto().getServicio().getUsuarioPrestador().getId().equalsIgnoreCase(authenticatedUsername);
    			
    			if (isServiceAccountOrAdministrator || isOwnerAndCustomer || isOwnerAndProvider)
    				contractService.setContractAsFinishedById(id);
    			else {
    				logger.warn("FinishContract - PUT - Request failed - User <{}> doesn't have access to contract <{}> in this context.", 
    						authenticatedUsername, id);
    				FieldError error = new FieldError("Authorization","error",
    						messageSource.getMessage("client.error.unauthorized", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
    			}
    			
    			logger.info("FinishContract - PUT - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.warn("FinishContract - PUT - Request failed - Unable to finish contract. Contract <{}> doesn't exist.", id);
	        	FieldError error = new FieldError("Contract","error",
	        			messageSource.getMessage("contract.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
	        }
		}
		catch (CustomResponseError e) {
			logger.warn("FinishContract - PUT - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("FinishContract - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Contract","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
	
	/*
	 * Cancela un contrato por parte del usuario final o usuario prestador.
	 */
	@RequestMapping(
			value = { "/contracts/{id}/cancel" }, 
			produces = "application/json; charset=UTF-8",			
			method = RequestMethod.PUT)
	@PreAuthorize("hasAuthority('USUARIO_FINAL')")
    public ResponseEntity<?> cancelContract(@PathVariable("id") Integer id) {
		logger.info("CancelContract - PUT - Processing request for contract <{}>.", id);
		try {
			Contract contract = contractService.getContractById(id);
			if (contract != null) {
				/* 
    			 * Validamos si el contrato siendo procesado le pertenezca 
    			 * al usuario autenticado (como usuario prestador, o como usuario final)
    			 */
    			String authenticatedUsername = 
    					((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    			boolean isServiceAccountOrAdministrator = 
    					userService.isServiceAccountOrAdministrator(userService.getUserById(authenticatedUsername));
    			boolean isOwnerAndCustomer = 
    					contract.getPresupuesto().getUsuarioFinal().getId().equalsIgnoreCase(authenticatedUsername);
    			boolean isOwnerAndProvider = 
    					contract.getPresupuesto().getServicio().getUsuarioPrestador().getId().equalsIgnoreCase(authenticatedUsername);
    			
    			if (isServiceAccountOrAdministrator || isOwnerAndCustomer)
    				contractService.customerCancelsContractById(id);
    			else if (isOwnerAndProvider)
    				contractService.providerCancelsContractById(id);
    			else {
    				logger.warn("CancelContract - PUT - Request failed - User <{}> doesn't have access to contract <{}> in this context.", 
    						authenticatedUsername, id);
    				FieldError error = new FieldError("Authorization","error",
    						messageSource.getMessage("client.error.unauthorized", null, Locale.getDefault()));
					return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.FORBIDDEN);
    			}
    			
    			logger.info("CancelContract - PUT - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.warn("CancelContract - PUT - Request failed - Unable to cancel contract. Contract <{}> doesn't exist.", id);
	        	FieldError error = new FieldError("Contract","error",
	        			messageSource.getMessage("contract.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
	        }
		}
		catch (CustomResponseError e) {
			logger.warn("CancelContract - PUT - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("CancelContract - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Contract","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
	
	/*
	 * Archiva un contrato.
	 */
	@RequestMapping(
			value = { "/contracts/{id}/archive" }, 
			produces = "application/json; charset=UTF-8",			
			method = RequestMethod.PUT)
	@PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> archiveContract(@PathVariable("id") Integer id) {
		logger.info("ArchiveContract - PUT - Processing request for contract <{}>.", id);
		try {
			if (contractService.getContractById(id) != null) {
				contractService.archiveContractById(id);
	        	
	        	logger.info("ArchiveContract - PUT - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.warn("ArchiveContract - POST - Request failed - Unable to archive contract. Contract <{}> doesn't exist.", id);
	        	FieldError error = new FieldError("Contract","error",
	        			messageSource.getMessage("contract.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
	        }
		}
		catch (CustomResponseError e) {
			logger.warn("ArchiveContract - PUT - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("ArchiveContract - PUT - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Contract","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
	
	/*
	 * Elimina físicamente un contrato.
	 */
	@RequestMapping(
			value = { "/contracts/{id}" }, 
			produces = "application/json; charset=UTF-8",			
			method = RequestMethod.DELETE)
	@PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<?> deleteContract(@PathVariable("id") Integer id) {
		logger.info("DeleteContract - DELETE - Processing request for contract <{}>.", id);
		try {
			if (contractService.getContractById(id) != null) {
				contractService.deleteContractById(id);
	        	
	        	logger.info("DeleteContract - DELETE - Exiting method, providing response resource to client.");
	            return new ResponseEntity<>(HttpStatus.OK);
	        }
	        else {
	        	logger.warn("DeleteContract - DELETE - Request failed - Unable to delete contract. Contract <{}> doesn't exist.", id);
	        	FieldError error = new FieldError("Contract","error",
	        			messageSource.getMessage("contract.doesnt.exist", new Integer[]{id}, Locale.getDefault()));
	        	return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.NOT_FOUND);
	        }
		}
		catch (CustomResponseError e) {
			logger.warn("DeleteContract - DELETE - Request failed - Validation error(s) detected.");
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(e), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			logger.error("DeleteContract - DELETE - Request failed - Error procesing request: ", e);
			FieldError error = new FieldError("Contract","error",messageSource.getMessage("server.error", null, Locale.getDefault()));
			return new ResponseEntity<>(miscUtils.getFormatedResponseError(error), HttpStatus.INTERNAL_SERVER_ERROR);
		}  
    }
}
