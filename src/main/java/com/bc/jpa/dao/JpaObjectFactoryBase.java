package com.bc.jpa.dao;

import com.bc.jpa.dao.functions.CommitEntityTransaction;
import com.bc.jpa.dao.sql.SQLDateTimePatterns;
import com.bc.jpa.dao.util.DatabaseFormat;
import com.bc.jpa.dao.util.DatabaseFormatImpl;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 * @author hp
 */
public class JpaObjectFactoryBase implements JpaObjectFactory{

//    private transient static final Logger LOG = Logger.getLogger(JpaObjectFactoryBase.class.getName());

    private final EntityManagerFactory entityManagerFactory;
    
    private final SQLDateTimePatterns sqlDateTimePatterns;
    
    public JpaObjectFactoryBase(
            EntityManagerFactory emf,
            SQLDateTimePatterns sqlDateTimePatterns) { 
        
        this.entityManagerFactory = Objects.requireNonNull(emf);
        
        this.sqlDateTimePatterns = Objects.requireNonNull(sqlDateTimePatterns);
    }
    
    @Override
    public <R> R execute(EntityManager em, Function<EntityManager, R> action, boolean closeEntityManager) {
        
        R result;

        final EntityTransaction t = em.getTransaction();

        try{
            
            t.begin();
            
            result = action.apply(em);
            
            this.commit(t);
            
        }finally{
            if(closeEntityManager && em.isOpen()) {
                em.close();
            }
        }

        return result;
    }

    @Override
    public boolean commit(EntityTransaction t) {
        return new CommitEntityTransaction().apply(t);
    }

    @Override
    public boolean isOpen() {
        try{
            entityManagerFactoryLock.lock();
            final EntityManagerFactory emf = this.getEntityManagerFactory();
            return emf != null && emf.isOpen();
        }finally{
            entityManagerFactoryLock.unlock();
        }
    }

    @Override
    public void close() {
        try{
            entityManagerFactoryLock.lock();
            final EntityManagerFactory emf = this.getEntityManagerFactory();
            if(emf != null && this.isOpen()) {
                emf.close();
            }
        }finally{
            entityManagerFactoryLock.unlock();
        }
    }
    
    @Override
    public DatabaseFormat getDatabaseFormat() {
        return new DatabaseFormatImpl(this, this.sqlDateTimePatterns);
    }

    @Override
    public EntityManager getEntityManager() {
        return this.getEntityManagerFactory().createEntityManager();
    }

    @Override
    public EntityManagerFactory reset() {
        this.clear();
        return getEntityManagerFactory();
    }
    
    private final Lock entityManagerFactoryLock = new ReentrantLock();
    
    @Override
    public void clear() {
        final EntityManagerFactory emf = this.getEntityManagerFactory();
        try{
            entityManagerFactoryLock.lock();
            if(emf != null) {
                emf.getCache().evictAll();
                emf.close();
            }
        }finally{
            entityManagerFactoryLock.unlock();
        }
    }
    
    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return this.entityManagerFactory;
    }

    public SQLDateTimePatterns getSqlDateTimePatterns() {
        return sqlDateTimePatterns;
    }

    public Lock getEntityManagerFactoryLock() {
        return entityManagerFactoryLock;
    }
}

