package com.yotereparo.dao;

import java.io.Serializable;

import java.lang.reflect.ParameterizedType;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Clase base para implementaciones de DAOs. 
 * Brinda wrappers comunes para operaciones con hibernate.
 * 
 * @author Rodrigo Yanis
 * 
 */
public abstract class AbstractDao<PK extends Serializable, T> {
	private final Class<T> persistentClass;
	
	@SuppressWarnings("unchecked")
	public AbstractDao(){
        this.persistentClass =(Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }
     
    @Autowired
    private SessionFactory sessionFactory;
 
    protected Session getSession(){
        return sessionFactory.getCurrentSession();
    }
 
    public T getByKey(PK key) {
        return (T) getSession().get(persistentClass, key);
    }
 
    public void persist(T entity) {
        getSession().persist(entity);
    }
    
    public void saveOrUpdate(T entity) {
        getSession().saveOrUpdate(entity);
    }
 
    public void delete(T entity) {
        getSession().delete(entity);
    }
}
