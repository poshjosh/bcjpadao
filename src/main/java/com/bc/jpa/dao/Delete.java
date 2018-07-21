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

import javax.persistence.criteria.CriteriaDelete;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 17, 2016 4:12:23 PM
 * @param <T>
 */
public interface Delete<T> 
        extends CriteriaForDelete<T>,
        DeleteDao<T> {
    
    CriteriaDelete getCriteriaDelete();
}
