package com.yotereparo.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.yotereparo.model.Contract;

/**
 * Capa de acceso a datos para Contratos. Su objetivo es abstraer la interacción con la API de hibernate.
 * 
 * @author Rodrigo Yanis
 * 
 */
@Repository
public class ContractDaoImpl extends AbstractDao<Integer, Contract> implements ContractDao {

	public Contract getContractById(Integer id) {
		return getByKey(id);
	}
	
	public void createContract(Contract contract) {
		persist(contract);
	}

	public void deleteContractById(Integer id) {
		Query<?> query = getSession().createSQLQuery("DELETE FROM contrato WHERE id_contrato = :id");
		query.setParameter("id", id);
        query.executeUpdate();
	}
	
	public List<Contract> getAllContracts() {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<Contract> criteriaQuery = builder.createQuery(Contract.class);
		criteriaQuery.from(Contract.class);
		
		return getSession().createQuery(criteriaQuery).getResultList();
	}
}
