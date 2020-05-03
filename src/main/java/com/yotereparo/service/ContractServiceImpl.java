package com.yotereparo.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yotereparo.dao.ContractDaoImpl;
import com.yotereparo.model.Contract;
import com.yotereparo.model.Quote;
import com.yotereparo.util.error.CustomResponseError;

/**
 * Capa de servicio para Contratos.
 * El objetivo de la misma es servir de interfaz entre el modelo y la capa de acceso a datos,
 * expone métodos para uso público en el contexto de la aplicación.
 * 
 * Implementa lógica de negocio donde correspondiera.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Service("contractService")
@Transactional
public class ContractServiceImpl implements ContractService {
	
	private static final Logger logger = LoggerFactory.getLogger(ContractServiceImpl.class);
	
	Set<String> transitionalStates = new HashSet<String>(
			Arrays.asList(Contract.PENDING_EXECUTION, Contract.ONGOING_EXECUTION));
	Set<String> finalStates = new HashSet<String>(
			Arrays.asList(Contract.ALREADY_EXECUTED, Contract.CANCELED_BY_CUSTOMER, Contract.CANCELED_BY_PROVIDER));
	
	@Autowired
	private ContractDaoImpl dao;
	@Autowired
    private MessageSource messageSource;

	@Override
	public void createContract(Quote quote) throws CustomResponseError {
		if (quote != null) {
			Contract contract = new Contract();
			contract.setPresupuesto(quote);
			contract.setFechaInicioEjecucion(quote.getFechaInicioEjecucionPropuesta());
			contract.setFechaFinEjecucion(quote.getFechaFinEjecucionPropuesta());
			contract.setPrecioFinal(quote.getPrecioTotal());
			contract.setFechaCreacion(new DateTime());
			contract.setEstado(Contract.PENDING_EXECUTION);
			
			try { 
				logger.info(String.format("Commiting creation of contract between "
						+ "customer <%s> and provider <%s> for service <%s>.",
							quote.getUsuarioFinal().getId(),
							quote.getServicio().getUsuarioPrestador().getId(),
							quote.getServicio().getId()));
			
				dao.persist(contract);
			}
			catch (Exception e) {
				logger.error("Contract creation failed - Caught exception: "+e.getMessage());
		        throw new RuntimeException(e.getMessage());
			}
		}
		else {
			// Illegal
			logger.warn("Contract creation failed - null quote received.");
			throw new CustomResponseError("Contract","error",messageSource.getMessage(
					"contract.creation.error", null, Locale.getDefault()));
		}
	}

	@Override
	public void refreshContractStatus(Contract contract) {
		if (contract != null) {
			logger.trace(String.format("Refreshing status of contract <%s>", contract.getId()));
			Boolean contractWasUpdated = false;
			String currentStatus = contract.getEstado();
			if (contract.getFechaInicioEjecucion().isBeforeNow()) {
				if (currentStatus.equals(Contract.PENDING_EXECUTION)) {
					if (contract.getFechaFinEjecucion() != null && contract.getFechaFinEjecucion().isBeforeNow()) 
						contract.setEstado(Contract.ALREADY_EXECUTED);
					else
						contract.setEstado(Contract.ONGOING_EXECUTION);
					dao.saveOrUpdate(contract);
					contractWasUpdated = true;
				}
				else if (currentStatus.equals(Contract.ONGOING_EXECUTION))
					if (contract.getFechaFinEjecucion() != null && contract.getFechaFinEjecucion().isBeforeNow()) { 
						contract.setEstado(Contract.ALREADY_EXECUTED);
						dao.saveOrUpdate(contract);
						contractWasUpdated = true;
					}
			}
			if (contractWasUpdated)
				logger.debug(String.format("Status of contract <%s> has been updated", contract.getId()));
		}
	}
	
	@Override
	public void setContractAsFinishedById(Integer id) {
		Contract entity = getContractById(id);
		if (entity.getEstado().equals(Contract.ONGOING_EXECUTION)) {
			logger.info(String.format("Setting contract <%s> as FINISHED", entity.getId()));
			entity.setEstado(Contract.ALREADY_EXECUTED);
		}
		else {
			// Illegal
			logger.warn(String.format("Contract <%s> can't be set as finished - "
					+ "incompatible Contract status: <%s>", 
					entity.getId(), entity.getEstado()));
			throw new CustomResponseError("Contract","estado",messageSource.getMessage(
					"contract.illegal.modification", null, Locale.getDefault()));
		}
	}
	
	@Override
	public void customerCancelsContractById(Integer id) {
		Contract entity = getContractById(id);
		if (entity.getEstado().equals(Contract.PENDING_EXECUTION)) {
			logger.info(String.format("Setting contract <%s> as CANCELED BY CUSTOMER", entity.getId()));
			entity.setEstado(Contract.CANCELED_BY_CUSTOMER);
		}
		else {
			// Illegal
			logger.warn(String.format("Contract <%s> can't be set as canceled by customer - "
					+ "incompatible Contract status: <%s>", 
					entity.getId(), entity.getEstado()));
			throw new CustomResponseError("Contract","estado",messageSource.getMessage(
					"contract.illegal.modification", null, Locale.getDefault()));
		}
	}

	@Override
	public void providerCancelsContractById(Integer id) {
		Contract entity = getContractById(id);
		if (entity.getEstado().equals(Contract.PENDING_EXECUTION)) {
			logger.info(String.format("Setting contract <%s> as CANCELED BY PROVIDER", entity.getId()));
			entity.setEstado(Contract.CANCELED_BY_PROVIDER);
		}
		else {
			// Illegal
			logger.warn(String.format("Contract <%s> can't be set as canceled by provider - "
					+ "incompatible Contract status: <%s>", 
					entity.getId(), entity.getEstado()));
			throw new CustomResponseError("Contract","estado",messageSource.getMessage(
					"contract.illegal.modification", null, Locale.getDefault()));
		}
	}

	@Override
	public void rateContractById(Integer contractId, Integer rate, String description) {
		Contract entity = getContractById(contractId);
		if (entity.getEstado().equals(Contract.ALREADY_EXECUTED)) {
			if (rate != null) {
				if (rate != entity.getValoracion()) {
					logger.debug(
							String.format("Updating attribute 'Valoracion' from contract <%s>", entity.getId()));
					entity.setValoracion(rate);
					
					logger.debug(
							String.format("Updating attribute 'FechaValoracion' from contract <%s>", entity.getId()));
					entity.setFechaValoracion(new DateTime());
				}
				if ((description == null && entity.getDescripcionValoracion() != null) || 
					(description != null && !description.equalsIgnoreCase(entity.getDescripcionValoracion()))) {
					logger.debug(
							String.format("Updating attribute 'DescripcionValoracion' "
									+ "from contract <%s>", entity.getId()));
					entity.setDescripcionValoracion(description);
				}
			}
			else
				if (entity.getValoracion() != null) {
					// Illegal
					logger.debug(
							String.format("Contract <%s> rated value: <%s> can't be erased!", 
									entity.getId(), entity.getValoracion()));
					throw new CustomResponseError(
							"Contract","valoracion",messageSource.getMessage(
									"contract.valoracion.cant.be.deleted", null, Locale.getDefault()));
				}
		}
		else {
			// Illegal
			logger.warn(String.format("Contract <%s> can't be rated - incompatible Contract status: <%s>", 
					entity.getId(), entity.getEstado()));
			throw new CustomResponseError("Contract","estado",messageSource.getMessage(
					"contract.illegal.modification", null, Locale.getDefault()));
		}
	}
	
	@Override
	public void archiveContractById(Integer id) {
		Contract entity = getContractById(id);
		if (finalStates.contains(entity.getEstado()))
			entity.setEstado(Contract.ARCHIVED);
		else {
			// Illegal
			logger.warn(String.format("Contract <%s> can't be set as archived - "
					+ "incompatible Contract status: <%s>", 
					entity.getId(), entity.getEstado()));
			throw new CustomResponseError("Contract","estado",messageSource.getMessage(
					"contract.illegal.modification", null, Locale.getDefault()));
		}
	}

	@Override
	public void deleteContractById(Integer id) {
		logger.info(String.format("Commiting deletion of contract <%s>", id));
		dao.deleteContractById(id);
	}
	
	@Override
	public Contract getContractById(Integer id) {
		logger.debug(String.format("Fetching contract <%s>", id));
		Contract contract = dao.getByKey(id);
		if (contract != null && transitionalStates.contains(contract.getEstado()))
			refreshContractStatus(contract);
		return contract;
	}

	@Override
	public List<Contract> getAllContracts() {
		logger.debug("Fetching all contracts");
		List<Contract> contracts = dao.getAllContracts();
		if (contracts != null)
			contracts.forEach(contract -> {
				if (transitionalStates.contains(contract.getEstado()))
					refreshContractStatus(contract);
			});
		
		return contracts;
	}
}
