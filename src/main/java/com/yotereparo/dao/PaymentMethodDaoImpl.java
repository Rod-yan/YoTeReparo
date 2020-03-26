package com.yotereparo.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Repository;

import com.yotereparo.model.PaymentMethod;

/**
 * Capa de acceso a datos para Medios de Pago. Su objetivo es abstraer la interacción con la API de hibernate.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Repository
public class PaymentMethodDaoImpl extends AbstractDao<Integer, PaymentMethod> implements PaymentMethodDao {

	public PaymentMethod getPaymentMethodById(Integer id) {
		return getByKey(id);
	}
	
	public PaymentMethod getPaymentMethodByDescription(String description) {
		PaymentMethod paymentMethod = getSession().byNaturalId(PaymentMethod.class)
				.using("descripcion", description)
                .load();
		
		return paymentMethod;
	}
 
	public List<PaymentMethod> getAllPaymentMethods() {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<PaymentMethod> criteriaQuery = builder.createQuery(PaymentMethod.class);
		criteriaQuery.from(PaymentMethod.class);
		
		return getSession().createQuery(criteriaQuery).getResultList();
	}
}