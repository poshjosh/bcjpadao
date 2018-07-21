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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Attribute;

/**
 * @param <T>
 * @author Chinomso Bassey Ikwuagwu on Aug 17, 2016 3:35:08 PM
 */
public interface CriteriaForSelect<T> 
       extends Criteria<CriteriaQuery<T>, TypedQuery<T>, T, Select<T>> {
    
    Select<T> distinct(boolean b);
    
    Select<T> search(String query, Collection<String> cols);
    
    Select<T> search(Class entityType, String query, Collection<String> cols);
    
    default Select<T> search(String query, Attribute... cols) {
        return this.search(query, this.getAttributeNames(cols));
    }
    
    Select<T> search(String query, String... cols);    
    
    default Select<T> search(Class entityType, String query, Attribute... cols) {
        return this.search(entityType, query, this.getAttributeNames(cols));
    }

    Select<T> search(Class entityType, String query, String... cols);

    default Select<T> select(Attribute... cols) {
        return this.select(this.getAttributeNames(cols));
    }

    Select<T> select(String... cols);
    
    default Select<T> select(Class fromType, Attribute... cols) {
        return this.select(fromType, this.getAttributeNames(cols));
    }

    Select<T> select(Class fromType, String... cols);

    Select<T> select(Collection<String> cols);
    
    Select<T> select(Class fromType, Collection<String> cols);
    
    default Select<T> sum(Attribute... cols) {
        return this.sum(this.getAttributeNames(cols));
    }
    
    Select<T> sum(String... cols);
    
    default Select<T> sum(Class entityType, Attribute... cols) {
        return this.sum(entityType, this.getAttributeNames(cols));
    }
    
    Select<T> sum(Class entityType, String... cols);

    Select<T> sum(Collection<String> cols);
    
    Select<T> sum(Class entityType, Collection<String> cols);
    
    Select<T> count();
    
    Select<T> count(Class entityType);
    
    default Select<T> count(Attribute col) {
        return count(col.getName());
    }

    Select<T> count(String col);

    default Select<T> count(Class entityType, Attribute col) {
        return count(entityType, col.getName());
    }
    
    Select<T> count(Class entityType, String col);

    default Select<T> max(Attribute col) {
        return max(col.getName());
    }

    Select<T> max(String col);

    default Select<T> max(Class entityType, Attribute col) {
        return max(entityType, col.getName());
    }
    
    Select<T> max(Class entityType, String col);
}
