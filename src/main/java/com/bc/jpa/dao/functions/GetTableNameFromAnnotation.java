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

package com.bc.jpa.dao.functions;

import java.io.Serializable;
import java.util.function.Function;
import javax.persistence.Table;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 9, 2018 10:53:59 PM
 */
public class GetTableNameFromAnnotation implements Function<Class, String>, Serializable {

    /**
     * Get the table name from the Table annotation or throw Exception
     * @param aClass
     * @return
     * @throws IllegalArgumentException 
     */
    @Override
    public String apply(Class aClass) throws IllegalArgumentException{
        final Table table = (Table)aClass.getAnnotation(Table.class);
        final String name = table == null ? null : table.name();
        if(name == null) {
            throw new IllegalArgumentException();
        }
        return name;
    }
}
