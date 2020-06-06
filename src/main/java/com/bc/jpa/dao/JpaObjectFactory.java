/*
 * Copyright 2018 NUROX Ltd.
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
import com.bc.jpa.dao.search.TextSearch;
import com.bc.jpa.dao.search.TextSearchImpl;
import com.bc.jpa.dao.sql.SQLDateTimePatterns;
import com.bc.jpa.dao.util.DatabaseFormat;
import com.bc.jpa.dao.util.EntityMemberAccess;
import com.bc.jpa.dao.util.EntityMemberAccessImpl;
import com.bc.jpa.dao.util.EntityReference;
import com.bc.jpa.dao.util.EntityReferenceImpl;
import java.util.Properties;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 18, 2018 3:22:08 PM
 */
public interface JpaObjectFactory{
    
    interface Builder {
        
        String JPA_VENDOR_ECLIPELINK = "eclipselink";
        String SQL_VENDOR_MYSQL = "mysql";
        
        JpaObjectFactory build();

        JpaObjectFactory.Builder classLoader(ClassLoader classLoader);

        JpaObjectFactory.Builder entityManagerFactoryCreator(EntityManagerFactoryCreator entityManagerFactoryCreator);

        boolean isBuildAttempted();

        JpaObjectFactory.Builder jpaVendor(String vendor);

        JpaObjectFactory.Builder persistenceUnitName(String persistenceUnitName);

        JpaObjectFactory.Builder properties(Properties properties);

        JpaObjectFactory.Builder properties(String location);

        JpaObjectFactory.Builder property(String name, String value);

        JpaObjectFactory.Builder sqlDateTimePatterns(SQLDateTimePatterns sqlDateTimePatterns);

        JpaObjectFactory.Builder sqlVendor(String vendor);
    }
    
    public static JpaObjectFactory.Builder builder() {
        return new JpaObjectFactoryBuilderImpl();
    }

    EntityManagerFactory reset();
    
    void clear();
    
    default <R> R execute(Function<EntityManager, R> action) {
        return this.execute(this.getEntityManager(), action, true);
    }
    
    <R> R execute(EntityManager em, Function<EntityManager, R> action, boolean closeEntityManager);
    
    boolean commit(EntityTransaction t);
    
    boolean isOpen();
    
    void close();
    
    EntityManagerFactory getEntityManagerFactory();  
        
    default Dao getDao() {
        return getDao(this.getEntityManager(), this.getDatabaseFormat());
    }

    default Dao getDao(EntityManager em, DatabaseFormat fmt) {
        return new DaoImpl(em, fmt);
    }

    default <T> Select<T> getDaoForSelect(Class<T> resultType) {
        return getDaoForSelect(this.getEntityManager(), resultType, this.getDatabaseFormat());
    }

    default <T> Select<T> getDaoForSelect(EntityManager em, Class<T> resultType, DatabaseFormat fmt) {
        return new SelectImpl<>(em, resultType, fmt);
    }

    default <T> Update<T> getDaoForUpdate(Class<T> entityType) {
        return getDaoForUpdate(this.getEntityManager(), entityType, this.getDatabaseFormat());
    }

    default <T> Update<T> getDaoForUpdate(EntityManager em, Class<T> entityType, DatabaseFormat fmt) {
        return new UpdateImpl<>(em, entityType, fmt);
    }

    default <T> Delete<T> getDaoForDelete(Class<T> entityType) {
        return getDaoForDelete(this.getEntityManager(), entityType, this.getDatabaseFormat());
    }
    
    default <T> Delete<T> getDaoForDelete(EntityManager em, Class<T> entityType, DatabaseFormat fmt) {
        return new DeleteImpl<>(em, entityType, fmt);
    }

    default <E> EntityMemberAccess<E, Object> getEntityMemberAccess(Class<E> entityClass) {
        return new EntityMemberAccessImpl(this, entityClass);
    }
    
    default EntityReference getEntityReference() {
        return new EntityReferenceImpl(this);
    }

    default TextSearch getTextSearch() {
        return new TextSearchImpl(this);
    }

    DatabaseFormat getDatabaseFormat();
    
    EntityManager getEntityManager();
}
