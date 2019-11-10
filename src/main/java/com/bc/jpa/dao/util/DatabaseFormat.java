package com.bc.jpa.dao.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @(#)DatabaseParameters.java   18-Apr-2015 14:04:35
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public interface DatabaseFormat {

    default Map toDatabaseFormat(final Class entityClass, Map params) {
        Map output;
        if(params == null) {
            output = null;
        }else{
            final Object NO_VALUE = new Object();
            output = new HashMap(params.size()+1, 1.0f);
            final Set keys = params.keySet();
            for(Object key:keys) {
                Object val = this.toDatabaseFormat(entityClass, key, params.get(key), NO_VALUE);
                if(val != NO_VALUE) {
                    output.put(key, val);
                }
            }
        }
        return output;
    }

    Object toDatabaseFormat(Class entityType, Object column, Object value, Object outputIfNone);
    
    boolean isDatabaseColumn(Class entityType, Object column);
}
