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

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Attribute;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 17, 2016 5:41:02 PM
 * @param <T>
 */
public interface CriteriaForUpdate<T> 
       extends Criteria<CriteriaUpdate<T>, Query, T, Update<T>> {

    default Update<T> set(Attribute col, Object value) {
        return this.set(col.getName(), value);
    }
    
    Update<T> set(String col, Object value);
    
    default Update<T> set(Class entityType, Attribute col, Object value) {
        return this.set(entityType, col.getName(), value);
    }

    Update<T> set(Class entityType, String col, Object value);    
}
