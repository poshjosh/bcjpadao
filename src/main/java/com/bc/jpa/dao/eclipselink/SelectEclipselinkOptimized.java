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
package com.bc.jpa.dao.eclipselink;

import com.bc.jpa.dao.DatabaseFormat;
import com.bc.jpa.dao.SelectImpl;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 18, 2016 2:01:52 AM
 * @param <T>
 */
public class SelectEclipselinkOptimized<T> extends SelectImpl<T> {
    
    private final EclipselinkReadOnlyOptimization<T> readonlyOptimization;

    public SelectEclipselinkOptimized(EntityManager em) {
        super(em);
        this.readonlyOptimization = new EclipselinkReadOnlyOptimization<>();
    }

    public SelectEclipselinkOptimized(EntityManager em, Class<T> resultType) {
        super(em, resultType);
        this.readonlyOptimization = new EclipselinkReadOnlyOptimization<>();
    }

    public SelectEclipselinkOptimized(EntityManager em, Class<T> resultType, DatabaseFormat databaseFormat) {
        super(em, resultType, databaseFormat);
        this.readonlyOptimization = new EclipselinkReadOnlyOptimization<>();
    }

    @Override
    public TypedQuery<T> format(TypedQuery<T> tq) {
        return this.readonlyOptimization.apply(this.getEntityTypes(), tq);
    }
}
