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

import com.bc.jpa.search.BaseSearchResults;
import com.bc.jpa.search.SearchResults;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 17, 2016 10:51:16 PM
 * @param <T>
 */
public interface SelectDao<T> 
        extends CriteriaDao<CriteriaQuery<T>, TypedQuery<T>, T, Select<T>> {
    
    Class<T> getResultType();

    Set<String> getSelectedColumns();
    
    @Override
    CriteriaForSelect<T> getCriteria();
    
    /**
     * @see #getResultsAndClose(int, int) 
     * @return The List of results
     */
    List<T> getResultsAndClose();
    
    /**
     * Calls {@link #getResultList() getResultList()}, {@link #commit() commit()}
     * and {@link #close() close()}; in that order.
     * @param firstResult Return results starting at this offset of the result list
     * @param maxResults  Return at most this number of results
     * @return The List of results
     */
    List<T> getResultsAndClose(int firstResult, int maxResults);

    /**
     * Calls {@link #getSingleResult() getSingleResult()}, {@link #commit() commit()}
     * and {@link #close() close()}; in that order.
     * @return The single result
     */
    T getSingleResultAndClose();

    default SearchResults<T> search(Class<T> entityClass, int pageSize) {
        final SelectDao<T> select = this.getCriteria().from(entityClass);
        return new BaseSearchResults(select, pageSize, true);
    }
    
    default List<T> findAllAndClose(Class<T> entityClass) {
        final List<T> resultList = this.getCriteria().from(entityClass).getResultsAndClose();
        return resultList;
    }
    
    default List<T> findAllAndClose(Class<T> entityClass, int offset, int limit) {
        final List<T> resultList = this.getCriteria().from(entityClass).getResultsAndClose(offset, limit);
        return resultList;
    }

    T findAndClose(Object primaryKey);
    
    T findAndClose(Object primaryKey, Map<String, Object> properties);
    
    T findAndClose(Object primaryKey, LockModeType lockMode);
    
    T findAndClose(Object primaryKey, LockModeType lockMode, Map<String, Object> properties);    
}
