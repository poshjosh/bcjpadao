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
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Apr 8, 2018 12:44:00 AM
 */
public class ConvertToEntityFieldNumberType implements Serializable {

    private transient static final Logger LOG = Logger.getLogger(ConvertToEntityFieldNumberType.class.getName());
    
    private final BiFunction<Object, Class, Object> toNumber;
    
    public ConvertToEntityFieldNumberType() {
        this.toNumber = new ConvertToNumber();
    }    

    public Object apply(Class entityType, String col, Object val, Object outputIfNone) {
        Objects.requireNonNull(entityType);
        Objects.requireNonNull(col);
        Objects.requireNonNull(val);
                
        Object ret = null;
        
        try{
            
            final Field field = entityType.getDeclaredField(col);
            
            if(field != null) {
                
                final Class fieldType = field.getType();
                final Class valType = val.getClass();
                
                LOG.finer(() -> "Field type: " + fieldType + ", value type: " + valType);
                
                if(!Objects.equals(fieldType, valType)) {
                    
                    ret = this.toNumber.apply(val, fieldType);
                }
            }
        }catch(RuntimeException | NoSuchFieldException ignored) { 
            LOG.fine(() -> ignored + "\nAbove exception encountered for: " + entityType.getName() + '#' + col);
        }
        
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "{0} {1} converted to {2} {3}",
                    new Object[]{val.getClass().getName(), val, 
                        ret == null ? null : ret.getClass().getName(), ret});
        }
        
        return ret == null ? outputIfNone : ret;
    }
}
