package com.bc.jpa.dao;

import com.bc.jpa.dao.functions.CommitEntityTransaction;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 19, 2016 2:18:50 AM
 */
public class DaoImpl implements Dao {

    private transient static final Logger LOG = Logger.getLogger(DaoImpl.class.getName());

    private final EntityManager entityManager;
    
    private final DatabaseFormat databaseFormat;
    
    private final Function<EntityTransaction, Boolean> commitTransaction;
        
    private boolean beginMethodCalled;
    
    public DaoImpl(EntityManager em) {
        
        this(em, null);
    }

    public DaoImpl(EntityManager em, DatabaseFormat databaseFormat) {
        
        this.entityManager = Objects.requireNonNull(em);

        this.databaseFormat = databaseFormat;
        
        this.commitTransaction = new CommitEntityTransaction();
    }
    
    @Override
    public <T> SelectDao<T> selectInstance(Class<T> resultType) {
        return this.forSelect(resultType);
    }

    @Override
    public <T> UpdateDao<T> updateInstance(Class<T> entityType) {
        return this.forUpdate(entityType);
    }

    @Override
    public <T> DeleteDao<T> deleteInstance(Class<T> entityType) {
        return this.forDelete(entityType);
    }
    
    @Override
    public <T> Select<T> forSelect(Class<T> resultType) {
        return new SelectImpl(this.entityManager, resultType, this.databaseFormat);
    }

    @Override
    public <T> Update<T> forUpdate(Class<T> entityType) {
        return new UpdateImpl(this.entityManager, entityType, this.databaseFormat);
    }

    @Override
    public <T> Delete<T> forDelete(Class<T> entityType) {
        return new DeleteImpl(this.entityManager, entityType, this.databaseFormat);
    }
    
    protected void clear() { 
        this.beginMethodCalled = false;
    }
    
    @Override
    public Dao begin() {
//        System.out.println("---------------- BEGINING --------------- @"+this.getClass().getName());
        final EntityTransaction t = this.entityManager.getTransaction();
        t.begin();
        this.beginMethodCalled = true;
        return (Dao)this;
    }
    
    @Override
    public void commit() {
        this.beginMethodCalled = false;
        EntityTransaction t = this.entityManager.getTransaction();
        this.commitTransaction.apply(t);
    }
    
    @Override
    public void close() {
//        System.out.println("----------------  CLOSING  --------------- @"+this.getClass().getName());
        if(this.entityManager.isOpen()) {
            this.entityManager.close();
        }
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
            if(!this.isBeginMethodCalled()) {
                this.begin();
            }
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
            if(!this.isBeginMethodCalled()) {
                this.begin();
            }
            R result = entityManager.merge(entity);
            this.commitIfBeginMethodCalled(result);
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
    public void removeAndClose(Object entity) {
        try{
            if(!this.isBeginMethodCalled()) {
                this.begin();
            }
            entityManager.remove(entity); 
            if(this.isBeginMethodCalled()) { 
                this.commit();
            }
        }finally{
            this.close();
        }
    }

    @Override
    public Dao refresh(Object entity) {
        entityManager.refresh(entity);
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
