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

import com.bc.jpa.dao.functions.EntityManagerFactoryCreator;
import com.bc.jpa.dao.functions.EntityManagerFactoryCreatorImpl;
import com.bc.jpa.dao.sql.SQLDateTimePatterns;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.EntityManagerFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 12:21:14 PM
 */
public class JpaObjectFactoryImpl extends JpaObjectFactoryBase implements Serializable {

//    private transient static final Logger LOG = Logger.getLogger(JpaObjectFactoryImpl.class.getName());

    private final String persistenceUnitName;
    
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
        
        super(emfCreator.newInstance(persistenceUnit), sqlDateTimePatterns);
        
        this.persistenceUnitName = Objects.requireNonNull(persistenceUnit);
        
        this.entityManagerFactoryCreator = Objects.requireNonNull(emfCreator);
    }

    private transient EntityManagerFactory _emf;
    
    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        try{
            this.getEntityManagerFactoryLock().lock();
            EntityManagerFactory output = super.getEntityManagerFactory();
            if(output == null) {
                if(this._emf == null) {
                    this._emf = this.entityManagerFactoryCreator.newInstance(this.persistenceUnitName);
                }
                output = this._emf;
            }
            return output;
        }finally{
            this.getEntityManagerFactoryLock().unlock();
        }
    }

    public EntityManagerFactoryCreator getEntityManagerFactoryCreator() {
        return entityManagerFactoryCreator;
    }

    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }
}
