package com.yotereparo.service;

import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

import com.yotereparo.dao.QuoteDaoImpl;
import com.yotereparo.model.Quote;
import com.yotereparo.model.Service;
import com.yotereparo.model.User;
import com.yotereparo.util.error.CustomResponseError;

/**
 * Capa de servicio para Presupuestos.
 * El objetivo de la misma es servir de interfaz entre el modelo y la capa de acceso a datos,
 * expone métodos para uso público en el contexto de la aplicación.
 * 
 * Implementa lógica de negocio donde correspondiera.
 * 
 * @author Rodrigo Yanis
 * 
 */
@org.springframework.stereotype.Service("quoteService")
@Transactional 
public class QuoteServiceImpl implements QuoteService {
	
	private static final Logger logger = LoggerFactory.getLogger(QuoteServiceImpl.class);
	
	@Autowired
	private QuoteDaoImpl dao;
	@Autowired
    private MessageSource messageSource;
	@Autowired
	private ContractService contractService; 
	
	@Override
	public void createQuote(Quote quote) {
		if (quote.getPrecioPresupuestado() != null) {
			// Illegal
			logger.debug("Quote's quoted price: setting a quoted price during creation is now allowed");
			throw new CustomResponseError("Quote","precioPresupuestado",
					messageSource.getMessage("quote.precioPresupuestado.not.allowed", null, Locale.getDefault()));
		}
		if (quote.getDescripcionRespuesta() != null) {
			// Illegal
			logger.debug("Quote's response description: setting a response description during creation is now allowed");
			throw new CustomResponseError("Quote","descripcionRespuesta",
					messageSource.getMessage("quote.descripcionRespuesta.not.allowed", null, Locale.getDefault()));
		}
		if (!quote.getServicio().getEstado().equals(Service.ACTIVE)) {
			// Illegal
			logger.debug("Quote's service: can't submit a quote for a service with status: <{}>", 
					quote.getServicio().getEstado());
			throw new CustomResponseError("Quote","servicio",
					messageSource.getMessage("quote.servicio.not.active", 
							new Integer[]{quote.getServicio().getId()}, Locale.getDefault()));
		}
		quote.setPrecioTotal();
		quote.setFechaSolicitud(new DateTime());
		quote.setFechaCreacion(new DateTime());
		quote.setEstado(Quote.AWAITING_PROVIDER);
		
		logger.info("Commiting creation of quote for service <{}> by user <{}>.", 
				quote.getServicio().getId(), quote.getUsuarioFinal().getId());
		dao.persist(quote);
	}
	
	@Override
	public void updateQuote(Quote quote) {
		Quote entity = getQuoteById(quote.getId());
		
		if (!entity.getEstado().equals(Quote.ARCHIVED) && 
			!entity.getEstado().equals(Quote.ACCEPTED_BY_CUSTOMER) &&
			!entity.getEstado().equals(Quote.REJECTED_BY_CUSTOMER) &&
			!entity.getEstado().equals(Quote.REJECTED_BY_PROVIDER)) {
			
			if (quote.getEstado().equals(entity.getEstado())) {
				// Illegal
				logger.debug("Quote <{}> status <{}> must change!", quote.getId(), entity.getEstado());
				throw new CustomResponseError("Quote","estado",
						messageSource.getMessage("quote.estado.must.change", null, Locale.getDefault()));
			}
			if (!quote.getUsuarioFinal().getId().equals(entity.getUsuarioFinal().getId())) {
				// Illegal
				logger.debug("Quote <{}> customer: <{}> can't be modified!", quote.getId(), entity.getUsuarioFinal().getId());
				throw new CustomResponseError("Quote","usuarioFinal",
						messageSource.getMessage("quote.usuarioFinal.cant.change", null, Locale.getDefault()));
			}
			if (!quote.getServicio().getId().equals(entity.getServicio().getId())) {
				// Illegal
				logger.debug("Quote <{}> service: <{}> can't be modified!", quote.getId(), entity.getServicio().getId());
				throw new CustomResponseError("Quote","servicio",
						messageSource.getMessage("quote.servicio.cant.change", null, Locale.getDefault()));
			}
			
			if (quote.getEstado().equals(Quote.AWAITING_PROVIDER)) {
				if (quote.getDescripcionSolicitud() != null) {
					if (!quote.getDescripcionSolicitud().equals(entity.getDescripcionSolicitud())) {
						logger.debug("Updating attribute 'DescripcionSolicitud' from quote <{}>", quote.getId());
						entity.setDescripcionSolicitud(quote.getDescripcionSolicitud());
					}
				}
				else
					if (entity.getDescripcionSolicitud() != null) {
						logger.debug("Updating attribute 'DescripcionSolicitud' from quote <{}>", quote.getId());
						entity.setDescripcionSolicitud(null);
					}
				if (quote.getDireccionUsuarioFinal() != null) {
					if (!quote.getDireccionUsuarioFinal().equals(entity.getDireccionUsuarioFinal())) {
						logger.debug("Updating attribute 'DireccionUsuarioFinal' from quote <{}>", quote.getId());
						entity.setDireccionUsuarioFinal(quote.getDireccionUsuarioFinal());
					}
				}
				else
					if (entity.getDireccionUsuarioFinal() != null) {
						logger.debug("Updating attribute 'DireccionUsuarioFinal' from quote <{}>", quote.getId());
						entity.setDireccionUsuarioFinal(null);
					}
			}
			
			if (quote.getEstado().equals(Quote.AWAITING_CUSTOMER)) {
				if (quote.getDescripcionRespuesta() != null) {
					if (!quote.getDescripcionRespuesta().equals(entity.getDescripcionRespuesta())) {
						logger.debug("Updating attribute 'DescripcionRespuesta' from quote <{}>", quote.getId());
						entity.setDescripcionRespuesta(quote.getDescripcionRespuesta());
					}
				}
				else
					if (entity.getDescripcionRespuesta() != null) {
						logger.debug("Updating attribute 'DescripcionRespuesta' from quote <{}>", quote.getId());
						entity.setDescripcionRespuesta(null);
					}
			}
			
			Boolean totalPriceIsChanging = false;
			if (quote.getEstado().equals(Quote.AWAITING_CUSTOMER)) {
				if (quote.getPrecioPresupuestado() != null) {
					if (quote.getPrecioPresupuestado() - entity.getPrecioPresupuestado() != 0) {
						if (quote.getPrecioPresupuestado() <= entity.getServicio().getPrecioMaximo() &&
							quote.getPrecioPresupuestado() >= entity.getServicio().getPrecioMinimo()) {
							logger.debug("Updating attribute 'PrecioPresupuestado' from quote <{}>", quote.getId());
							totalPriceIsChanging = true;
							entity.setPrecioPresupuestado(quote.getPrecioPresupuestado());
						}
						else {
							logger.debug("Quote <{}> quoted price: <{}> must be between <{}> (MIN) and <{}> (MAX)", 
									quote.getId(),
									entity.getPrecioPresupuestado(),
									entity.getServicio().getPrecioMinimo(),entity.getServicio().getPrecioMaximo());
							throw new CustomResponseError("Quote","precioPresupuestado",
									messageSource.getMessage("quote.precioPresupuestado.out.of.service.boundaries", 
											new Float[] {entity.getServicio().getPrecioMinimo(),
														 entity.getServicio().getPrecioMaximo()}, 
											Locale.getDefault()));
						}
					}
				}
				else 
					if (entity.getPrecioPresupuestado() != null) {
						// Illegal
						logger.debug("Quote <{}> quoted price: <{}> can't be erased!", 
								quote.getId(), entity.getPrecioPresupuestado());
						throw new CustomResponseError("Quote","precioPresupuestado",
								messageSource.getMessage("quote.precioPresupuestado.cant.be.deleted", null, Locale.getDefault()));
					}
					else {
						// Illegal
						logger.debug("Quote <{}> quoted price: <{}> must provide a value!", 
								quote.getId(), entity.getPrecioPresupuestado());
						throw new CustomResponseError("Quote","precioPresupuestado",
								messageSource.getMessage("quote.precioPresupuestado.not.null", null, Locale.getDefault()));
					}
			}
			
			if (!quote.getFechaInicioEjecucionPropuesta().equals(entity.getFechaInicioEjecucionPropuesta())) {
				logger.debug("Updating attribute 'FechaInicioEjecucionPropuesta' from quote <{}>", quote.getId());
				entity.setFechaInicioEjecucionPropuesta(quote.getFechaInicioEjecucionPropuesta());
			}
			
			if (quote.getFechaFinEjecucionPropuesta() != null) {
				if (!quote.getFechaFinEjecucionPropuesta().equals(entity.getFechaFinEjecucionPropuesta())) {
					logger.debug("Updating attribute 'FechaFinEjecucionPropuesta' from quote <{}>", quote.getId());
					entity.setFechaFinEjecucionPropuesta(quote.getFechaFinEjecucionPropuesta());
				}
			}
			else 
				if (entity.getFechaFinEjecucionPropuesta() != null) {
					logger.debug("Updating attribute 'FechaFinEjecucionPropuesta' from quote <{}>", quote.getId());
					entity.setFechaFinEjecucionPropuesta(null);
				}
			
			if (quote.isIncluyeInsumos() != entity.isIncluyeInsumos()) {
				logger.debug("Updating attribute 'IncluyeInsumos' from quote <{}>", quote.getId());
				totalPriceIsChanging = true;
				entity.setIncluyeInsumos(quote.isIncluyeInsumos());
			}
			
			if (quote.isIncluyeAdicionales() != entity.isIncluyeAdicionales()) {
				logger.debug("Updating attribute 'IncluyeAdicionales' from quote <{}>", quote.getId());
				totalPriceIsChanging = true;
				entity.setIncluyeAdicionales(quote.isIncluyeAdicionales());
			}
			
			if (totalPriceIsChanging) {
				logger.debug("Updating attribute 'PrecioTotal' from quote <{}>", quote.getId());
				entity.setPrecioTotal();
			}
			
			if (quote.getEstado() != null) {
				if (!quote.getEstado().equals(entity.getEstado())) {
					if (quote.getEstado().equals(Quote.AWAITING_CUSTOMER)) {
						logger.debug("Updating attribute 'FechaRespuesta' from quote <{}>", quote.getId());
						entity.setFechaRespuesta(new DateTime());
					}
					else if (quote.getEstado().equals(Quote.AWAITING_PROVIDER)) {
						logger.debug("Updating attribute 'FechaSolicitud' from quote <{}>", quote.getId());
						entity.setFechaSolicitud(new DateTime());
					}
					logger.debug("Updating attribute 'Estado' from quote <{}>", quote.getId());
					entity.setEstado(quote.getEstado());
				}
			}
			else
				if (entity.getEstado() != null) {
					// Illegal
					logger.debug("Quote <{}> state: <{}> can't be erased!", quote.getId(), entity.getEstado());
					throw new CustomResponseError("Quote","estado",
							messageSource.getMessage("quote.estado.cant.be.deleted", null, Locale.getDefault()));
				}
			
			logger.info("Commiting update for quote <{}>", quote.getId());
		}
		else {
			// Illegal
			logger.debug("Quote <{}> can't me modified - incompatible Quote status: <{}>", 
					quote.getId(), quote.getEstado());
			throw new CustomResponseError("Quote","estado",
					messageSource.getMessage("quote.illegal.modification", null, Locale.getDefault()));
		}
	}
	
	@Override
	public void customerAcceptsQuoteById(Integer id) {
		Quote quote = getQuoteById(id);
		if (quote != null && quote.getEstado().equals(Quote.AWAITING_CUSTOMER)) {
			DateTime fechaInicioEjecucion = quote.getFechaInicioEjecucionPropuesta();
			if (fechaInicioEjecucion == null  || 
				(fechaInicioEjecucion != null && (fechaInicioEjecucion.isAfter(new DateTime())))) {
				logger.info("Customer accepted quote <{}>", quote.getId());
				quote.setEstado(Quote.ACCEPTED_BY_CUSTOMER);
				
				// Crea el contrato
				contractService.createContract(quote);
			}
			else {
				// Illegal
				logger.debug("Quote <{}> can't be accepted by customer - proposed date for execution start has already passed", 
						quote.getId());
				throw new CustomResponseError("Quote","fechaInicioEjecucionPropuesta",
						messageSource.getMessage("quote.fechaInicioEjecucionPropuesta.already.passed", null, Locale.getDefault()));
			}
		}
		else {
			// Illegal
			logger.debug("Quote <{}> can't be accepted by customer - incompatible Quote status: <{}>", 
					quote.getId(), quote.getEstado());
			throw new CustomResponseError("Quote","estado",
					messageSource.getMessage("quote.illegal.modification", null, Locale.getDefault()));
		}
	}

	@Override
	public void customerRejectsQuoteById(Integer id) {
		Quote quote = getQuoteById(id);
		if (quote != null && quote.getEstado().equals(Quote.AWAITING_CUSTOMER)) {
			logger.info("Customer rejected quote <{}>", quote.getId());
			quote.setEstado(Quote.REJECTED_BY_CUSTOMER);
		}
		else {
			// Illegal
			logger.debug("Quote <{}> can't be rejected by customer - incompatible Quote status: <{}>", 
					quote.getId(), quote.getEstado());
			throw new CustomResponseError("Quote","estado",
					messageSource.getMessage("quote.illegal.modification", null, Locale.getDefault()));
		}
	}
	
	@Override
	public void providerRejectsQuoteById(Integer id) {
		Quote quote = getQuoteById(id);
		if (quote != null && 
				(quote.getEstado().equals(Quote.AWAITING_CUSTOMER) || 
				 quote.getEstado().equals(Quote.AWAITING_PROVIDER))) {
			logger.info("Provider rejected quote <{}>", quote.getId());
			quote.setEstado(Quote.REJECTED_BY_PROVIDER);
		}
		else {
			// Illegal
			logger.debug("Quote <{}> can't be rejected by provider - incompatible Quote status: <{}>", 
					quote.getId(), quote.getEstado());
			throw new CustomResponseError("Quote","estado",
					messageSource.getMessage("quote.illegal.modification", null, Locale.getDefault()));
		}
	}

	@Override
	public void archiveQuoteById(Integer id) {
		Quote quote = getQuoteById(id);
		if (quote != null && 
				(quote.getEstado().equals(Quote.ACCEPTED_BY_CUSTOMER) || 
				 quote.getEstado().equals(Quote.REJECTED_BY_CUSTOMER) || 
				 quote.getEstado().equals(Quote.REJECTED_BY_PROVIDER))) {
			logger.info("Archiving quote <{}>", quote.getId());
			quote.setEstado(Quote.ARCHIVED);
		}
		else {
			// Illegal
			logger.debug("Quote <{}> can't be archived - incompatible Quote status: <{}>", 
					quote.getId(), quote.getEstado());
			throw new CustomResponseError("Quote","estado",
					messageSource.getMessage("quote.illegal.modification", null, Locale.getDefault()));
		}
	}

	@Override
	public void deleteQuoteById(Integer id) {
		logger.info("Commiting deletion of quote <{}>", id);
		dao.deleteQuoteById(id);
	}

	@Override
	public Quote getQuoteById(Integer id) {
		logger.debug("Fetching quote <{}>", id);
		return dao.getByKey(id);
	}
	
	@Override
	public Boolean activeQuoteExistBetween(User user, Service service) {
		logger.debug("Validating existence of an active quotation process between user <{}> and service <{}>", 
				user.getId(), service.getId());
		if (user != null && service != null)
			for (Quote presupuesto : user.getPresupuestos())
				if (presupuesto.getServicio().getId() == service.getId())
					if (presupuesto.getEstado().equals(Quote.AWAITING_CUSTOMER) || 
						presupuesto.getEstado().equals(Quote.AWAITING_PROVIDER))
						return true;
		return false;
	}
	
	@Override
	public Boolean quoteExistBetween(User user, Service service) {
		logger.debug("Validating existence of an active quotation process between user <{}> and service <{}>", 
				user.getId(), service.getId());
		if (user != null && service != null)
			for (Quote presupuesto : user.getPresupuestos())
				if (presupuesto.getServicio().getId() == service.getId())
					return true;
		return false;
	}

	@Override
	public List<Quote> getAllQuotes() {
		logger.debug("Fetching all quotes");
		return dao.getAllQuotes();
	}
}
