/*
 * Copyright 2017 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.jpa.dao;

import com.bc.jpa.dao.functions.CommitEntityTransaction;
import com.bc.jpa.dao.util.DatabaseFormatImpl;
import com.bc.jpa.dao.util.DatabaseFormat;
import com.bc.jpa.dao.functions.EntityManagerFactoryCreator;
import com.bc.jpa.dao.functions.EntityManagerFactoryCreatorImpl;
import com.bc.jpa.dao.sql.SQLDateTimePatterns;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 12:21:14 PM
 */
public class JpaObjectFactoryImpl implements JpaObjectFactory, Serializable {

//    private transient static final Logger LOG = Logger.getLogger(JpaObjectFactoryImpl.class.getName());

    private final String persistenceUnitName;
    
    private final SQLDateTimePatterns sqlDateTimePatterns;
    
    private EntityManagerFactoryCreator entityManagerFactoryCreator;
    
    public JpaObjectFactoryImpl(
            String persistenceUnit,
            SQLDateTimePatterns sqlDateTimePatterns) { 
        this(persistenceUnit, new EntityManagerFactoryCreatorImpl(), sqlDateTimePatterns);
    }
    
    public JpaObjectFactoryImpl(
            String persistenceUnit,
            EntityManagerFactoryCreator emfCreator,
            SQLDateTimePatterns sqlDateTimePatterns) { 
        
        this.persistenceUnitName = Objects.requireNonNull(persistenceUnit);
        
        this.entityManagerFactoryCreator = Objects.requireNonNull(emfCreator);
        
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
        return this._emf != null && this._emf.isOpen();
    }

    @Override
    public void close() {
        if(this._emf != null && this.isOpen()) {
            this._emf.close();
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

    private final Lock lock = new ReentrantLock();
    
    private transient EntityManagerFactory _emf;
    
    @Override
    public EntityManagerFactory reset() {
        this.clear();
        return getEntityManagerFactory();
    }
    
    @Override
    public void clear() {
        try{
            lock.lock();
            if(this._emf != null) {
                this._emf.getCache().evictAll();
                this._emf.close();
                this._emf = null;
            }
        }finally{
            lock.unlock();
        }
    }
    
    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        try{
            lock.lock();
            if(this._emf == null) {
                this._emf = this.entityManagerFactoryCreator.newInstance(this.persistenceUnitName);
            }
            return this._emf;
        }finally{
            lock.unlock();
        }
    }

    public EntityManagerFactoryCreator getEntityManagerFactoryCreator() {
        return entityManagerFactoryCreator;
    }

    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    public SQLDateTimePatterns getSqlDateTimePatterns() {
        return sqlDateTimePatterns;
    }
}
