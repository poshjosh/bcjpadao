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

package com.bc.jpa.dao.functions;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 2:01:57 PM
 */
public class GetColumnNamesOfType implements BiFunction<Class, Class, Collection<String>>, Serializable {

    private transient static final Logger LOG = Logger.getLogger(GetColumnNamesOfType.class.getName());

    private final EntityManagerFactory emf;
    
    public GetColumnNamesOfType(EntityManagerFactory emf) { 
        this.emf = Objects.requireNonNull(emf);
    }
    
    @Override
    public Collection<String> apply(Class entityType, Class columnType) {
        
        final Set<String> output = new LinkedHashSet();
        
        final List<String> columnNames = this.getColumnNames(entityType);
        
        for(String columnName : columnNames) {
            
            final Field field;
            try{
                field = entityType.getDeclaredField(columnName);
            }catch(NoSuchFieldException e) {
                LOG.log(Level.WARNING, null, e);
                continue;
            }
            
            final Class columnClass = field.getType();
            
            if(columnType.isAssignableFrom(columnClass)) {
                
                output.add(columnName);
            }
        }
        
        return output;
    }
    
    public List<String> getColumnNames(Class entityType) {
        return new GetColumnNames(emf).apply(entityType);
    }
}
