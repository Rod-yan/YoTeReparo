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
	
	@Override
	public void createQuote(Quote quote) {
		if (quote.getPrecioPresupuestado() != null)
			if (!(quote.getPrecioPresupuestado() <= quote.getServicio().getPrecioMaximo() && quote.getPrecioPresupuestado() >= quote.getServicio().getPrecioMinimo())) {
				logger.debug(String.format("Quote <%s> quoted price: <%s> must be between <%s> (MIN) and <%s> (MAX)", quote.getId(), quote.getPrecioPresupuestado(), quote.getServicio().getPrecioMinimo(),quote.getServicio().getPrecioMaximo()));
				throw new CustomResponseError("Quote","precioPresupuestado",messageSource.getMessage("quote.precioPresupuestado.out.of.service.boundaries", new Float[] {quote.getServicio().getPrecioMinimo(),quote.getServicio().getPrecioMaximo()}, Locale.getDefault()));
			}
		quote.setPrecioTotal();
		quote.setFechaSolicitud(new DateTime());
		quote.setEstado(Quote.AWAITING_PROVIDER);
		
		logger.info("Commiting creation of quote.");
		dao.persist(quote);
	}
	
	@Override
	public void updateQuote(Quote quote) {
		Quote entity = getQuoteById(quote.getId());
		
		if (!entity.getEstado().equals(Quote.ARCHIVED) && !entity.getEstado().equals(Quote.ACCEPTED_BY_CUSTOMER) && !entity.getEstado().equals(Quote.REJECTED_BY_CUSTOMER) && !entity.getEstado().equals(Quote.REJECTED_BY_PROVIDER)) {
			if (!quote.getUsuarioFinal().getId().equals(entity.getUsuarioFinal().getId())) {
				// Illegal
				logger.debug(String.format("Quote <%s> customer: <%s> can't be modified!", quote.getId(), entity.getUsuarioFinal().getId()));
				throw new CustomResponseError("Quote","usuarioFinal",messageSource.getMessage("quote.usuarioFinal.cant.change", null, Locale.getDefault()));
			}
			
			if (!quote.getServicio().getId().equals(entity.getServicio().getId())) {
				// Illegal
				logger.debug(String.format("Quote <%s> service: <%s> can't be modified!", quote.getId(), entity.getServicio().getId()));
				throw new CustomResponseError("Quote","servicio",messageSource.getMessage("quote.servicio.cant.change", null, Locale.getDefault()));
			}
			
			if (quote.getEstado().equals(Quote.AWAITING_PROVIDER))
				if (quote.getDescripcionSolicitud() != null) {
					if (!quote.getDescripcionSolicitud().equals(entity.getDescripcionSolicitud())) {
						logger.debug(String.format("Updating attribute 'DescripcionSolicitud' from quote <%s>", quote.getId()));
						entity.setDescripcionSolicitud(quote.getDescripcionSolicitud());
					}
				}
				else
					if (entity.getDescripcionSolicitud() != null) {
						logger.debug(String.format("Updating attribute 'DescripcionSolicitud' from quote <%s>", quote.getId()));
						entity.setDescripcionSolicitud(null);
					}
			
			if (quote.getEstado().equals(Quote.AWAITING_CUSTOMER))
				if (quote.getDescripcionRespuesta() != null) {
					if (!quote.getDescripcionRespuesta().equals(entity.getDescripcionRespuesta())) {
						logger.debug(String.format("Updating attribute 'DescripcionRespuesta' from quote <%s>", quote.getId()));
						entity.setDescripcionRespuesta(quote.getDescripcionRespuesta());
					}
				}
				else
					if (entity.getDescripcionRespuesta() != null) {
						logger.debug(String.format("Updating attribute 'DescripcionRespuesta' from quote <%s>", quote.getId()));
						entity.setDescripcionRespuesta(null);
					}
			
			Boolean totalPriceIsChanging = false;
			if (quote.getEstado().equals(Quote.AWAITING_CUSTOMER))
				if (quote.getPrecioPresupuestado() != null) {
					if (quote.getPrecioPresupuestado() - entity.getPrecioPresupuestado() != 0) {
						if (quote.getPrecioPresupuestado() <= entity.getServicio().getPrecioMaximo() && quote.getPrecioPresupuestado() >= entity.getServicio().getPrecioMinimo()) {
							logger.debug(String.format("Updating attribute 'PrecioPresupuestado' from quote <%s>", quote.getId()));
							totalPriceIsChanging = true;
							entity.setPrecioPresupuestado(quote.getPrecioPresupuestado());
						}
						else {
							logger.debug(String.format("Quote <%s> quoted price: <%s> must be between <%s> (MIN) and <%s> (MAX)", quote.getId(), entity.getPrecioPresupuestado(), entity.getServicio().getPrecioMinimo(),entity.getServicio().getPrecioMaximo()));
							throw new CustomResponseError("Quote","precioPresupuestado",messageSource.getMessage("quote.precioPresupuestado.out.of.service.boundaries", new Float[] {entity.getServicio().getPrecioMinimo(),entity.getServicio().getPrecioMaximo()}, Locale.getDefault()));
						}
					}
				}
				else 
					if (entity.getPrecioPresupuestado() != null) {
						// Illegal
						logger.debug(String.format("Quote <%s> quoted price: <%s> can't be erased!", quote.getId(), entity.getPrecioPresupuestado()));
						throw new CustomResponseError("Quote","precioPresupuestado",messageSource.getMessage("quote.precioPresupuestado.cant.be.deleted", null, Locale.getDefault()));
					}
					else {
						// Illegal
						logger.debug(String.format("Quote <%s> quoted price: <%s> must provide a value!", quote.getId(), entity.getPrecioPresupuestado()));
						throw new CustomResponseError("Quote","precioPresupuestado",messageSource.getMessage("quote.precioPresupuestado.not.null", null, Locale.getDefault()));
					}
			
			if (quote.getFechaInicioEjecucionPropuesta() != null) {
				if (!quote.getFechaInicioEjecucionPropuesta().equals(entity.getFechaInicioEjecucionPropuesta())) {
					logger.debug(String.format("Updating attribute 'FechaInicioEjecucionPropuesta' from quote <%s>", quote.getId()));
					entity.setFechaInicioEjecucionPropuesta(quote.getFechaInicioEjecucionPropuesta());
				}
			}
			else 
				if (entity.getFechaInicioEjecucionPropuesta() != null) {
					logger.debug(String.format("Updating attribute 'FechaInicioEjecucionPropuesta' from quote <%s>", quote.getId()));
					entity.setFechaInicioEjecucionPropuesta(null);
				}
			
			if (quote.isIncluyeInsumos() != entity.isIncluyeInsumos()) {
				logger.debug(String.format("Updating attribute 'IncluyeInsumos' from quote <%s>", quote.getId()));
				totalPriceIsChanging = true;
				entity.setIncluyeInsumos(quote.isIncluyeInsumos());
			}
			
			if (quote.isIncluyeAdicionales() != entity.isIncluyeAdicionales()) {
				logger.debug(String.format("Updating attribute 'IncluyeAdicionales' from quote <%s>", quote.getId()));
				totalPriceIsChanging = true;
				entity.setIncluyeAdicionales(quote.isIncluyeAdicionales());
			}
			
			if (totalPriceIsChanging) {
				logger.debug(String.format("Updating attribute 'PrecioTotal' from quote <%s>", quote.getId()));
				entity.setPrecioTotal();
			}
			
			if (quote.getEstado() == null || quote.getEstado().equals(entity.getEstado())) {
				// Illegal
				logger.debug(String.format("Quote <%s> state <%s> must change!", quote.getId(), entity.getEstado()));
				throw new CustomResponseError("Quote","estado",messageSource.getMessage("quote.estado.must.change", null, Locale.getDefault()));
			}
			if (quote.getEstado() != null) {
				if (!quote.getEstado().equals(entity.getEstado())) {
					if (quote.getEstado().equals(Quote.AWAITING_CUSTOMER)) {
						logger.debug(String.format("Updating attribute 'FechaRespuesta' from quote <%s>", quote.getId()));
						entity.setFechaRespuesta(new DateTime());
					}
					else if (quote.getEstado().equals(Quote.AWAITING_PROVIDER)) {
						logger.debug(String.format("Updating attribute 'FechaSolicitud' from quote <%s>", quote.getId()));
						entity.setFechaSolicitud(new DateTime());
					}
					logger.debug(String.format("Updating attribute 'Estado' from quote <%s>", quote.getId()));
					entity.setEstado(quote.getEstado());
				}
			}
			else
				if (entity.getEstado() != null) {
					// Illegal
					logger.debug(String.format("Quote <%s> state: <%s> can't be erased!", quote.getId(), entity.getEstado()));
					throw new CustomResponseError("Quote","estado",messageSource.getMessage("quote.estado.cant.be.deleted", null, Locale.getDefault()));
				}
			
			logger.info(String.format("Commiting update for quote <%s>", quote.getId()));
		}
		else {
			// Illegal
			logger.debug(String.format("Quote <%s> can't me modified - object is in a final state", quote.getId()));
			throw new CustomResponseError("Quote","estado",messageSource.getMessage("quote.illegal.modification", null, Locale.getDefault()));
		}
	}
	
	@Override
	public void customerAcceptsQuote(Integer id) {
		Quote quote = getQuoteById(id);
		if (quote != null && quote.getEstado().equals(Quote.AWAITING_CUSTOMER)) {
			logger.info(String.format("Customer accepted quote <%s>", quote.getId()));
			quote.setEstado(Quote.ACCEPTED_BY_CUSTOMER);
			// TODO: Generar Contrato
		}
		else {
			// Illegal
			logger.debug(String.format("Quote <%s> can't be accepted by customer - incompatible Quote status: <%s>", quote.getId(), quote.getEstado()));
			throw new CustomResponseError("Quote","estado",messageSource.getMessage("quote.illegal.modification", null, Locale.getDefault()));
		}
	}

	@Override
	public void customerRejectsQuote(Integer id) {
		Quote quote = getQuoteById(id);
		if (quote != null && quote.getEstado().equals(Quote.AWAITING_CUSTOMER)) {
			logger.info(String.format("Customer rejected quote <%s>", quote.getId()));
			quote.setEstado(Quote.REJECTED_BY_CUSTOMER);
		}
		else {
			// Illegal
			logger.debug(String.format("Quote <%s> can't be rejected by customer - incompatible Quote status: <%s>", quote.getId(), quote.getEstado()));
			throw new CustomResponseError("Quote","estado",messageSource.getMessage("quote.illegal.modification", null, Locale.getDefault()));
		}
	}
	
	@Override
	public void providerRejectsQuote(Integer id) {
		Quote quote = getQuoteById(id);
		if (quote != null && (quote.getEstado().equals(Quote.AWAITING_CUSTOMER) || quote.getEstado().equals(Quote.AWAITING_PROVIDER))) {
			logger.info(String.format("Provider rejected quote <%s>", quote.getId()));
			quote.setEstado(Quote.REJECTED_BY_PROVIDER);
		}
		else {
			// Illegal
			logger.debug(String.format("Quote <%s> can't be rejected by provider - incompatible Quote status: <%s>", quote.getId(), quote.getEstado()));
			throw new CustomResponseError("Quote","estado",messageSource.getMessage("quote.illegal.modification", null, Locale.getDefault()));
		}
	}

	@Override
	public void archiveQuoteById(Integer id) {
		Quote quote = getQuoteById(id);
		if (quote != null && (quote.getEstado().equals(Quote.ACCEPTED_BY_CUSTOMER) || quote.getEstado().equals(Quote.REJECTED_BY_CUSTOMER) || quote.getEstado().equals(Quote.REJECTED_BY_PROVIDER))) {
			logger.info(String.format("Archiving quote <%s>", quote.getId()));
			quote.setEstado(Quote.ARCHIVED);
		}
		else {
			// Illegal
			logger.debug(String.format("Quote <%s> can't be archived - incompatible Quote status: <%s>", quote.getId(), quote.getEstado()));
			throw new CustomResponseError("Quote","estado",messageSource.getMessage("quote.illegal.modification", null, Locale.getDefault()));
		}
	}

	@Override
	public void deleteQuoteById(Integer id) {
		logger.info(String.format("Commiting deletion of quote <%s>", id));
		dao.deleteQuoteById(id);
	}

	@Override
	public Quote getQuoteById(Integer id) {
		logger.debug(String.format("Fetching quote <%s>", id));
		return dao.getByKey(id);
	}
	
	@Override
	public Boolean activeQuoteExistBetween(User user, Service service) {
		logger.debug(String.format("Validating existance of an active quotation process between user <%s> and service <%s>", user.getId(), service.getId()));
		if (user != null && service != null)
			for (Quote presupuesto : user.getPresupuestos())
				if (presupuesto.getServicio().getId() == service.getId())
					if (presupuesto.getEstado().equals(Quote.AWAITING_CUSTOMER) || presupuesto.getEstado().equals(Quote.AWAITING_PROVIDER))
						return true;
		return false;
	}
	
	@Override
	public Boolean quoteExistBetween(User user, Service service) {
		logger.debug(String.format("Validating existance of an active quotation process between user <%s> and service <%s>", user.getId(), service.getId()));
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
