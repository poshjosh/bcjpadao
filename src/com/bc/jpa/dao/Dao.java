package com.bc.jpa.dao;

import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 19, 2016 2:28:44 AM
 */
public interface Dao extends AutoCloseable {
    
    <T> SelectDao<T> forSelect(Class<T> resultType);
    
    <T> UpdateDao<T> forUpdate(Class<T> entityType);
    
    <T> DeleteDao<T> forDelete(Class<T> entityType);

    <T> BuilderForSelect<T> builderForSelect(Class<T> resultType);
    
    <T> BuilderForUpdate<T> builderForUpdate(Class<T> entityType);
    
    <T> BuilderForDelete<T> builderForDelete(Class<T> entityType);
    
    EntityManager getEntityManager();
    
    Dao begin();
    
    void commit();
    
    @Override
    void close();
    
    boolean isOpen();
    
    Dao persist(Object entity);
    
    void persistAndClose(Object entity);

    <R> R merge(R entity);
    
    <R> R mergeAndClose(R entity);

    Dao remove(Object entity);
    
    void removeAndClose(Object entity);
    
    <R> R find(Class<R> entityClass, Object primaryKey);
    
    <R> R findAndClose(Class<R> entityClass, Object primaryKey);
    
    <R> R find(Class<R> entityClass, Object primaryKey, Map<String, Object> properties);
    
    <R> R findAndClose(Class<R> entityClass, Object primaryKey, Map<String, Object> properties);
    
    <R> R find(Class<R> entityClass, Object primaryKey, LockModeType lockMode);
    
    <R> R findAndClose(Class<R> entityClass, Object primaryKey, LockModeType lockMode);

    <R> R find(Class<R> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties);    
    
    <R> R findAndClose(Class<R> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties);    
    
    Dao detach(Object entity);

    boolean contains(Object entity);
}
