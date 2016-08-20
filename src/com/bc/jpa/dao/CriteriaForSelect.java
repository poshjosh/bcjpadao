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

/**
 * @param <T>
 * @author Chinomso Bassey Ikwuagwu on Aug 17, 2016 3:35:08 PM
 */
public interface CriteriaForSelect<T> 
       extends Criteria<CriteriaQuery<T>, TypedQuery<T>, T, BuilderForSelect<T>> {
    
    BuilderForSelect<T> distinct(boolean b);
    
    BuilderForSelect<T> search(String query, Collection<String> cols);
    
    BuilderForSelect<T> search(Class entityType, String query, Collection<String> cols);
    
    BuilderForSelect<T> search(String query, String... cols);    
    
    BuilderForSelect<T> search(Class entityType, String query, String... cols);

    BuilderForSelect<T> select(String... cols);
    
    BuilderForSelect<T> select(Class fromType, String... cols);

    BuilderForSelect<T> select(Collection<String> cols);
    
    BuilderForSelect<T> select(Class fromType, Collection<String> cols);
    
    BuilderForSelect<T> sum(String... cols);
    
    BuilderForSelect<T> sum(Class entityType, String... cols);

    BuilderForSelect<T> sum(Collection<String> cols);
    
    BuilderForSelect<T> sum(Class entityType, Collection<String> cols);
    
    BuilderForSelect<T> count();
    
    BuilderForSelect<T> count(Class entityType);
    
    BuilderForSelect<T> count(String col);

    BuilderForSelect<T> count(Class entityType, String col);

    BuilderForSelect<T> max(String col);
    
    BuilderForSelect<T> max(Class entityType, String col);
}
