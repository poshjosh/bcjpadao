/*
 * Copyright 2016 NUROX Ltd.
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

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 17, 2016 5:46:05 PM
 */
public class BuilderForUpdateImpl<T> 
        extends BuilderForCriteriaDao<CriteriaUpdate<T>, Query, T, BuilderForUpdate<T>> 
        implements BuilderForUpdate<T> {
    
    private final CriteriaUpdate criteriaUpdate;

    public BuilderForUpdateImpl(EntityManager em, Class<T> entityType) {
        super(em, entityType);
        this.criteriaUpdate = this.getCriteriaBuilder().createCriteriaUpdate(entityType);
    }

    public BuilderForUpdateImpl(EntityManager em, Class<T> entityType, DatabaseFormat databaseFormat) {
        super(em, entityType, databaseFormat);
        this.criteriaUpdate = this.getCriteriaBuilder().createCriteriaUpdate(entityType);
    }

    @Override
    public CriteriaForUpdate<T> getCriteria() {
        return this;
    }

    /**
     * @return the update count
     */
    @Override
    public int executeUpdate() {
        return this.createQuery().executeUpdate();
    }
    
    /**
     * Calls {@link #executeUpdate() executeUpdate()}, {@link #commit() commit()} 
     * and {@link #close()}; in that order.
     * @return the update count
     */
    @Override
    public int finish() {
        try{
            int updateCount = this.executeUpdate();
            this.commit();
            return updateCount;
        }finally{
            this.close();
        }
    }
    
    @Override
    public BuilderForUpdate<T> set(String col, Object value) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.set(this.getCurrentEntityType(), col, value);
    }
    
    @Override
    public BuilderForUpdate<T> set(Class entityType, String col, Object value) {
        this.throwExceptionIfBuilt();
        this.throwExceptionIfNull(entityType,"Invalid call to #set(Class,String,Object) method, with a null Class argument");
        this.setCurrentEntityType(entityType);
        this.criteriaUpdate.set(col, value);
//        this.getCriteriaBuilder().nullLiteral(entityType);
        return this;
    }

    @Override
    protected void doOrderBy(Order order) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Root doFrom(Class entityClass) {
        return this.criteriaUpdate.from(entityClass);
    }

    @Override
    protected CriteriaUpdate<T> doBuild() {
        Predicate restriction = this.getRestriction();
        if(restriction != null) {
            criteriaUpdate.where(restriction);
        }
        return criteriaUpdate;
    }

    @Override
    protected Query doCreateQuery(CriteriaUpdate<T> criteriaUpdate) {
        return this.getEntityManager().createQuery(criteriaUpdate);
    }
}
