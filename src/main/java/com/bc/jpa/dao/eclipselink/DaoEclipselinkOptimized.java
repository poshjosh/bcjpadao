/*
 * Copyright 2017 NUROX Ltd.
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

package com.bc.jpa.dao.eclipselink;

import com.bc.jpa.dao.DaoImpl;
import com.bc.jpa.dao.DatabaseFormat;
import com.bc.jpa.dao.Select;
import javax.persistence.EntityManager;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 11:37:24 AM
 */
public class DaoEclipselinkOptimized extends DaoImpl {

    public DaoEclipselinkOptimized(EntityManager em) {
        super(em);
    }

    public DaoEclipselinkOptimized(EntityManager em, DatabaseFormat databaseFormat) {
        super(em, databaseFormat);
    }

    @Override
    public <T> Select<T> forSelect(Class<T> resultType) {
        return new SelectEclipselinkOptimized(this.getEntityManager(), resultType, this.getDatabaseFormat());
    }
}
