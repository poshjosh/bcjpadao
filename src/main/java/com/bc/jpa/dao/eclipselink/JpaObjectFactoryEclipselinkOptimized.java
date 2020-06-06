/*
 * Copyright 2018 NUROX Ltd.
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

import com.bc.jpa.dao.JpaObjectFactoryImpl;
import com.bc.jpa.dao.Select;
import com.bc.jpa.dao.eclipselink.SelectEclipselinkOptimized;
import com.bc.jpa.dao.functions.EntityManagerFactoryCreator;
import com.bc.jpa.dao.sql.SQLDateTimePatterns;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 18, 2018 5:06:45 PM
 */
public class JpaObjectFactoryEclipselinkOptimized extends JpaObjectFactoryImpl {

    public JpaObjectFactoryEclipselinkOptimized(
            String persistenceUnit, SQLDateTimePatterns sqlDateTimePatterns) {
        super(persistenceUnit, sqlDateTimePatterns);
    }

    public JpaObjectFactoryEclipselinkOptimized(
            String persistenceUnit, 
            EntityManagerFactoryCreator emfCreator, 
            SQLDateTimePatterns sqlDateTimePatterns) {
        super(persistenceUnit, emfCreator, sqlDateTimePatterns);
    }


    @Override
    public <T> Select<T> getDaoForSelect(Class<T> resultType) {
        return new SelectEclipselinkOptimized(this.getEntityManager(), resultType, this.getDatabaseFormat());
    }
}
