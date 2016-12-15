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
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 17, 2016 9:42:57 PM
 * @param <T>
 */
public class BuilderForDeleteImpl<T> 
        extends BuilderForCriteriaDao<CriteriaDelete<T>, Query, T, BuilderForDelete<T>> 
        implements BuilderForDelete<T> {
    
    private final CriteriaDelete criteriaDelete;

    public BuilderForDeleteImpl(EntityManager em, Class<T> entityType) {
        super(em, entityType);
        this.criteriaDelete = this.getCriteriaBuilder().createCriteriaDelete(entityType);
    }

    public BuilderForDeleteImpl(EntityManager em, Class<T> entityType, DatabaseFormat databaseFormat) {
        super(em, entityType, databaseFormat);
        this.criteriaDelete = this.getCriteriaBuilder().createCriteriaDelete(entityType);
    }

    @Override
    public CriteriaForDelete<T> getCriteria() {
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
    protected Root doFrom(Class entityClass) {
        return this.criteriaDelete.from(entityClass);
    }

    @Override
    protected CriteriaDelete<T> doBuild() {
        Predicate restriction = this.getRestriction();
        if(restriction != null) {
            criteriaDelete.where(restriction);
        }
        return criteriaDelete;
    }

    @Override
    protected Query doCreateQuery(CriteriaDelete<T> criteriaDelete) {
        return this.getEntityManager().createQuery(criteriaDelete);
    }
}
