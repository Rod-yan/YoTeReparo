package com.yotereparo.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yotereparo.model.Quote;

/**
 * Capa de acceso a datos para Presupuestos. Su objetivo es abstraer la interacción con la API de hibernate.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Repository
public class QuoteDaoImpl extends AbstractDao<Integer, Quote> implements QuoteDao {

	public Quote getQuoteById(Integer id) {
		return getByKey(id);
	}
	
	public void createQuote(Quote quote) {
		persist(quote);
	}

	public void deleteQuoteById(Integer id) {
		Query<?> query = getSession().createSQLQuery("DELETE FROM presupuesto WHERE id_presupuesto = :id");
		query.setParameter("id", id);
        query.executeUpdate();
	}
 
	public List<Quote> getAllQuotes() {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<Quote> criteriaQuery = builder.createQuery(Quote.class);
		criteriaQuery.from(Quote.class);
		
		return getSession().createQuery(criteriaQuery).getResultList();
	}
}
