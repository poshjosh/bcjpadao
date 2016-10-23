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

import java.util.Collection;
import java.util.Map;
import javax.persistence.Query;
import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.JoinType;

/**
 * @param <C> The type of output produced by the {@link #build() builder()}  
 * e.g CriteriaQuery, CriteriaDelete, CriteriaUpdate.
 * @param <Q> The type of {@link javax.persistence.Query Query} returned by this
 * {@link #createQuery() createQuery()} method.
 * @param <T> The type of the entity class
 * @param <D> The type of the {@link com.bc.jpa.dao.CriteriaDao CriteriaDao} returned by methods in this class
 * @author Chinomso Bassey Ikwuagwu on Aug 17, 2016 3:33:20 PM
 */
public interface Criteria<C extends CommonAbstractCriteria, Q extends Query, T, D extends CriteriaDao<C, Q, T, D>> {

    public static enum ComparisonOperator{
        EQUALS, NOT_EQUALS, LIKE, NOT_LIKE, GREATER_THAN, GREATER_OR_EQUALS, LESS_THAN, LESS_OR_EQUALS
    };
    
    public static enum LogicalOperator{AND, NOT, OR, XOR};
    
    ComparisonOperator EQUALS = ComparisonOperator.EQUALS;
    ComparisonOperator EQ = ComparisonOperator.EQUALS;
    ComparisonOperator NOT_EQUALS = ComparisonOperator.NOT_EQUALS;    
    ComparisonOperator NE = ComparisonOperator.NOT_EQUALS;
    ComparisonOperator LIKE = ComparisonOperator.LIKE;
    ComparisonOperator NOT_LIKE = ComparisonOperator.NOT_LIKE;
    ComparisonOperator GREATER_THAN = ComparisonOperator.GREATER_THAN;
    ComparisonOperator GT = ComparisonOperator.GREATER_THAN;
    ComparisonOperator GREATER_OR_EQUALS = ComparisonOperator.GREATER_OR_EQUALS;
    ComparisonOperator GTE = ComparisonOperator.GREATER_OR_EQUALS;
    ComparisonOperator LESS_THAN = ComparisonOperator.LESS_THAN;
    ComparisonOperator LT = ComparisonOperator.LESS_THAN;
    ComparisonOperator LESS_OR_EQUALS = ComparisonOperator.LESS_OR_EQUALS;
    ComparisonOperator LTE = ComparisonOperator.LESS_OR_EQUALS;
    LogicalOperator AND = LogicalOperator.AND;
//    LogicalOperator NOT = LogicalOperator.NOT;
    LogicalOperator OR = LogicalOperator.OR;
//    LogicalOperator XOR = LogicalOperator.XOR;
    
    D from(Class entityType);
    
    /**
     * @see #and(java.lang.Class) 
     * @return This instance
     */
    D and();
    
    /**
     * Switches the last connector for the specified entity type to AND
     * @param entityType The target entity type reference
     * @return This instance
     */
    D and(Class entityType);

    /**
     * @see #or(java.lang.Class) 
     * @return This instance
     */
    D or();
    
    /**
     * Switches the last connector for the specified entity type to OR
     * @param entityType The target entity type reference
     * @return This instance
     */
    D or(Class entityType);
    
    D where(String col, Object... values);
    
    D where(Class entityType, String col, Object... values);
    
    D where(Map parameters);
    
    D where(Class entityType, Map parameters);

    D where(Criteria.ComparisonOperator comparisonOperator, Criteria.LogicalOperator connector, Map params);
    
    D where(Class entityType, 
            Criteria.ComparisonOperator comparisonOperator, Criteria.LogicalOperator connector, Map params);
    
    D where(String col, Collection values);
    
    D where(Class entityType, String col, Collection values);

    D where(String key, 
            Criteria.ComparisonOperator comparisonOperator, Object val);
    
    D where(Class entityType, String key, 
            Criteria.ComparisonOperator comparisonOperator, Object val);

    D where(String [] cols, 
            Criteria.ComparisonOperator comparisonOperator, 
            Object val, Criteria.LogicalOperator connector);
    
    D where(Class entityType, String [] cols, 
            Criteria.ComparisonOperator comparisonOperator, 
            Object val, Criteria.LogicalOperator connector);

    D where(String key, 
            Criteria.ComparisonOperator comparisonOperator, Object val, Criteria.LogicalOperator connector);
    
    D where(Class entityType, String key, 
            Criteria.ComparisonOperator comparisonOperator, Object val, Criteria.LogicalOperator connector);

    D join(String joinColumn, Class toType);
    
    D join(Class fromType, String joinColumn, Class toType);

    D join(String joinColumn, JoinType joinType, Class toType);
    
    D join(Class fromType, String joinColumn, JoinType joinType, Class toType);

    D joins(JoinType joinType, Map<String, Class> joins);

    D joins(Class fromType, JoinType joinType, Map<String, Class> joins);

    D orderBy(String col, String order);
    
    D orderBy(Class entityType, String col, String order);

    D orderBy(Map<String, String> orders);
    
    D orderBy(Class entityType, Map<String, String> orders);
    
    D descOrder(String... cols);
    
    D descOrder(Class entityType, String... cols);

    D descOrder(Collection<String> cols);
    
    D descOrder(Class entityType, Collection<String> cols);
    
    D ascOrder(String... cols);
    
    D ascOrder(Class entityType, String... cols);

    D ascOrder(Collection<String> cols);
    
    D ascOrder(Class entityType, Collection<String> cols);
}
