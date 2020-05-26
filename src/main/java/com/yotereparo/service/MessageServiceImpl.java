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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yotereparo.dao.MessageDaoImpl;
import com.yotereparo.model.Message;
import com.yotereparo.util.error.CustomResponseError;

/**
 * Capa de servicio para Mensajes.
 * El objetivo de la misma es servir de interfaz entre el modelo y la capa de acceso a datos,
 * expone métodos para uso público en el contexto de la aplicación.
 * 
 * Implementa lógica de negocio donde correspondiera.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Service("messageService")
@Transactional
public class MessageServiceImpl implements MessageService {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
	
	Set<String> transitionalStates = new HashSet<String>(
			Arrays.asList(Message.AWAITING_RESPONSE));
	Set<String> finalStates = new HashSet<String>(
			Arrays.asList(Message.CLOSED, Message.EXPIRED));
	
	@Autowired
	private MessageDaoImpl dao;
	@Autowired
	private Environment environment;
	@Autowired
    private MessageSource messageSource;

	public void createMessage(Message message) {
		if (message.getConsulta() == null || message.getConsulta().isEmpty()) {
			// Illegal
			logger.debug("Message query: query can't be null or empty");
			throw new CustomResponseError("Message","respuesta",
					messageSource.getMessage("message.consulta.not.empty", null, Locale.getDefault()));
		}
		if (message.getRespuesta() != null) {
			// Illegal
			logger.debug("Message response: setting a response during creation is now allowed");
			throw new CustomResponseError("Message","respuesta",
					messageSource.getMessage("message.respuesta.not.allowed", null, Locale.getDefault()));
		}
		
		message.setFechaConsulta(new DateTime());
		message.setEstado(Message.AWAITING_RESPONSE);
		
		logger.info("Commiting creation of message for service <{}> by user <{}>.", 
				message.getServicio().getId(), message.getUsuarioFinal().getId());
		dao.persist(message);
	}
	
	public void refreshMessageStatus(Message message) {
		if (message != null) {
			logger.trace("Refreshing status of message <{}>", message.getId());
			if (message.getEstado().equals(Message.AWAITING_RESPONSE)) 
				// Los mensajes tienen un tiempo de vida de N días
				if (message.getFechaConsulta().isAfter(
					new DateTime().plusDays(Integer.parseInt(environment.getProperty("message.expiration.timeoffset.days"))))) {
					
					logger.debug("Status of message <{}> has been updated", message.getId());
					message.setEstado(Message.EXPIRED);
					dao.saveOrUpdate(message);
				}
		}
	}

	public void updateMessage(Message message) {
		Message entity = getMessageById(message.getId());
		
		if (entity.getEstado().equals(Message.AWAITING_RESPONSE)) {
			if (!message.getUsuarioFinal().getId().equals(entity.getUsuarioFinal().getId())) {
				// Illegal
				logger.debug("Message <{}> customer: <{}> can't be modified!", message.getId(), entity.getUsuarioFinal().getId());
				throw new CustomResponseError("Message","usuarioFinal",
						messageSource.getMessage("message.usuarioFinal.cant.change", null, Locale.getDefault()));
			}
			if (!message.getServicio().getId().equals(entity.getServicio().getId())) {
				// Illegal
				logger.debug("Message <{}> service: <{}> can't be modified!", message.getId(), entity.getServicio().getId());
				throw new CustomResponseError("Message","servicio",
						messageSource.getMessage("message.servicio.cant.change", null, Locale.getDefault()));
			}
			if (message.getConsulta() != null && !message.getConsulta().isEmpty()) {
				if (!message.getConsulta().equalsIgnoreCase(entity.getConsulta())) {
				// Illegal
				logger.debug("Message <{}> query can't be modified!", message.getId());
				throw new CustomResponseError("Message","consulta",
						messageSource.getMessage("message.consulta.cant.change", null, Locale.getDefault()));
				}
			}
			
			if (message.getRespuesta() != null && !message.getRespuesta().isEmpty()) {
				logger.debug("Updating attribute 'Respuesta' from message <{}>", message.getId());
				entity.setRespuesta(message.getRespuesta());
				
				logger.debug("Updating attribute 'FechaRespuesta' from message <{}>", message.getId());
				entity.setFechaRespuesta(new DateTime());
				
				logger.debug("Updating attribute 'Estado' from message <{}>", message.getId());
				entity.setEstado(Message.CLOSED);
			}
			else {
				// Illegal
				logger.debug("Message <{}> response can't be null or empty!", message.getId());
				throw new CustomResponseError("Message","respuesta",
						messageSource.getMessage("message.respuesta.not.empty", null, Locale.getDefault()));
			}
		}
		else if (entity.getEstado().equals(Message.EXPIRED)) {
			logger.debug("Message <{}> can't me modified - Message already expired", message.getId());
			throw new CustomResponseError("Message","estado",
					messageSource.getMessage("message.expired", null, Locale.getDefault()));
		}
		else {
			// Illegal
			logger.debug("Message <{}> can't me modified - incompatible Message status: <{}>", 
					message.getId(), message.getEstado());
			throw new CustomResponseError("Message","estado",
					messageSource.getMessage("message.illegal.modification", null, Locale.getDefault()));
		}
		
		logger.info("Commiting update for message <{}>", message.getId());
	}

	public void deleteMessageById(Integer id) {
		logger.info("Commiting deletion of message <{}>", id);
		dao.deleteMessageById(id);
	}
	
	public Message getMessageById(Integer id) {
		logger.debug("Fetching message by id <{}>", id);
		Message message = dao.getMessageById(id);
		if (message != null && transitionalStates.contains(message.getEstado()))
			refreshMessageStatus(message);
		return message;
	}
	
	public List<Message> getAllMessages() {
		logger.debug("Fetching all messages");
		List<Message> messages = dao.getAllMessages();
		if (messages != null)
			messages.forEach(message -> {
				if (transitionalStates.contains(message.getEstado()))
					refreshMessageStatus(message);
			});
		return messages;
	}
}
