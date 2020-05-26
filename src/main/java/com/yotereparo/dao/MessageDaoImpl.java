package com.yotereparo.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yotereparo.model.Message;

/**
 * Capa de acceso a datos para Mensajes. Su objetivo es abstraer la interacción con la API de hibernate.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Repository
public class MessageDaoImpl extends AbstractDao<Integer, Message> implements MessageDao {

	public Message getMessageById(Integer id) {
		return getByKey(id);
	}
	
	public void createMessage(Message message) {
		persist(message);
	}

	public void deleteMessageById(Integer id) {
		Query<?> query = getSession().createSQLQuery("DELETE FROM mensaje WHERE id_mensaje = :id");
		query.setParameter("id", id);
        query.executeUpdate();
	}
 
	public List<Message> getAllMessages() {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<Message> criteriaQuery = builder.createQuery(Message.class);
		criteriaQuery.from(Message.class);
		
		return getSession().createQuery(criteriaQuery).getResultList();
	}
}
