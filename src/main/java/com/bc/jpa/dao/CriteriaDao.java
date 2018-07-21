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

import java.util.Set;
import javax.persistence.Query;
import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.CriteriaBuilder;

/**
 * @param <C> The type of output produced by the {@link #build() builder()}  
 * e.g CriteriaQuery, CriteriaDelete, CriteriaUpdate.
 * @param <Q> The type of {@link javax.persistence.Query Query} returned by this
 * {@link #createQuery() createQuery()} method.
 * @param <T> The type of the entity class
 * @param <D> The type of the {@link com.bc.jpa.dao.CriteriaDao CriteriaDao} returned by methods in this class
 * @author Chinomso Bassey Ikwuagwu on Aug 17, 2016 4:12:48 PM
 */
public interface CriteriaDao<C extends CommonAbstractCriteria, Q extends Query, T, D extends CriteriaDao<C, Q, T, D>> 
        extends Dao {
    
    Criteria<C, Q, T, D> getCriteria();
    
    CriteriaBuilder getCriteriaBuilder();
    
    C build();
    
    Q createQuery();
    
    D reset();

    Set<Class> getEntityTypes();
    
    @Override
    D detach(Object entity);

    @Override
    D remove(Object entity);
    
    @Override
    D refresh(Object entity);

    @Override
    D persist(Object entity);

//    @Override
//    void commit();

    @Override
    D begin();
}
/**
 * 
    @Override
    D detach(Object entity);

    @Override
    D remove(Object entity);

    @Override
    D merge(Object entity);

    @Override
    D persist(Object entity);

//    @Override
//    void commit();

    @Override
    D begin();
 * 
 */