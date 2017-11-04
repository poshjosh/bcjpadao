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
import javax.persistence.criteria.CriteriaDelete;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 17, 2016 11:33:51 PM
 * @param <T>
 */
public interface DeleteDao<T> 
        extends CriteriaDao<CriteriaDelete<T>, Query, T, Delete<T>> {

    @Override
    CriteriaForDelete<T> getCriteria();
    
    /**
     * @return the update count
     */
    int executeUpdate();
    
    /**
     * Calls {@link #executeUpdate() executeUpdate()}, {@link #commit() commit()} 
     * and {@link #close() close()}; in that order.
     * @return the update count
     */
    int finish();
}
