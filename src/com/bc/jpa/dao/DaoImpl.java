package com.bc.jpa.dao;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 19, 2016 2:18:50 AM
 */
public class DaoImpl implements Dao {

    private transient static final Logger logger = Logger.getLogger(DaoImpl.class.getName());

    private final EntityManager entityManager;
    
    private final DatabaseFormat databaseFormat;
    
    private boolean beginMethodCalled;
        
    public DaoImpl(EntityManager em) {
        
        this(em, null);
    }

    public DaoImpl(EntityManager em, DatabaseFormat databaseFormat) {
        
        this.entityManager = Objects.requireNonNull(em);
        
        this.databaseFormat = databaseFormat;
    }
    
    @Override
    public <T> SelectDao<T> forSelect(Class<T> resultType) {
        return this.builderForSelect(resultType);
    }

    @Override
    public <T> UpdateDao<T> forUpdate(Class<T> entityType) {
        return this.builderForUpdate(entityType);
    }

    @Override
    public <T> DeleteDao<T> forDelete(Class<T> entityType) {
        return this.builderForDelete(entityType);
    }
    
    @Override
    public <T> BuilderForSelect<T> builderForSelect(Class<T> resultType) {
        return new BuilderForSelectImpl(this.entityManager, resultType, this.databaseFormat);
    }

    @Override
    public <T> BuilderForUpdate<T> builderForUpdate(Class<T> entityType) {
        return new BuilderForUpdateImpl(this.entityManager, entityType, this.databaseFormat);
    }

    @Override
    public <T> BuilderForDelete<T> builderForDelete(Class<T> entityType) {
        return new BuilderForDeleteImpl(this.entityManager, entityType, this.databaseFormat);
    }
    
    protected void clear() { 
        this.beginMethodCalled = false;
    }
    
    @Override
    public Dao begin() {
        final EntityTransaction t = this.entityManager.getTransaction();
        t.begin();
        this.beginMethodCalled = true;
        return (Dao)this;
    }
    
    @Override
    public void commit() {
        this.beginMethodCalled = false;
        EntityTransaction t = this.entityManager.getTransaction();
        try{
            if (t.isActive()) {
                if (t.getRollbackOnly()) {
                    t.rollback();
                } else {
                    t.commit();
                }
            }
        }finally{
            if(t.isActive()) {
                t.rollback();
            }
        }
    }
    
    @Override
    public void close() {
        this.entityManager.close();
    }
    
    @Override
    public boolean isOpen() {
        return this.entityManager.isOpen();
    }

    @Override
    public Dao persist(Object entity) {
        entityManager.persist(entity);
        return (Dao)this;
    }
    
    @Override
    public void persistAndClose(Object entity) {
        try{
            entityManager.persist(entity);
            if(this.isBeginMethodCalled()) { 
                this.commit();
            }
        }finally{
            this.close();
        }
    }

    @Override
    public <R> R merge(R entity) {
        return entityManager.merge(entity);
    }

    @Override
    public <R> R mergeAndClose(R entity) {
        try{
            R result = entityManager.merge(entity);
            if(this.isBeginMethodCalled()) { 
                this.commit();
            }
            return result;
        }finally{
            this.close();
        }
    }

    @Override
    public Dao remove(Object entity) {
        entityManager.remove(entity);
        return (Dao)this;
    }
    
    @Override
    public <R> R find(Class<R> entityClass, Object primaryKey) {
        R result = entityManager.find(entityClass, primaryKey);
        return this.commitIfBeginMethodCalled(result);
    }
    
    @Override
    public <R> R findAndClose(Class<R> entityClass, Object primaryKey) {
        try{
            return this.find(entityClass, primaryKey);
        }finally{
            this.close();
        }
    }
    
    @Override
    public <R> R find(Class<R> entityClass, Object primaryKey, Map<String, Object> properties) {
        R result = entityManager.find(entityClass, primaryKey, properties);
        return this.commitIfBeginMethodCalled(result);
    }
    
    @Override
    public <R> R findAndClose(Class<R> entityClass, Object primaryKey, Map<String, Object> properties) {
        try{
            return this.find(entityClass, primaryKey, properties);
        }finally{
            this.close();
        }
    }
    
    @Override
    public <R> R find(Class<R> entityClass, Object primaryKey, LockModeType lockMode) {
        R result = entityManager.find(entityClass, primaryKey, lockMode);
        return this.commitIfBeginMethodCalled(result);
    }

    @Override
    public <R> R findAndClose(Class<R> entityClass, Object primaryKey, LockModeType lockMode) {
        try{
            return this.find(entityClass, primaryKey, lockMode);
        }finally{
            this.close();
        }
    }
    
    @Override
    public <R> R find(Class<R> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        R result = entityManager.find(entityClass, primaryKey, lockMode, properties);
        return this.commitIfBeginMethodCalled(result);
    }

    @Override
    public <R> R findAndClose(Class<R> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        try{
            return this.find(entityClass, primaryKey, lockMode, properties);
        }finally{
            this.close();
        }
    }
    
    private <R> R commitIfBeginMethodCalled(R result) {
        if(this.isBeginMethodCalled()) {
            this.commit();
        }
        return result;
    }

    @Override
    public Dao detach(Object entity) {
        entityManager.detach(entity);
        return (Dao)this;
    }

    @Override
    public boolean contains(Object entity) {
        return entityManager.contains(entity);
    }

    public final boolean isBeginMethodCalled() {
        return beginMethodCalled;
    }

    @Override
    public final EntityManager getEntityManager() {
        return entityManager;
    }

    public final DatabaseFormat getDatabaseFormat() {
        return databaseFormat;
    }
}
