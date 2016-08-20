package com.bc.jpa.dao;


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

    Object toDatabaseFormat(Class entityType, Object column, Object value, Object outputIfNone);
    
    boolean isDatabaseColumn(Class entityType, Object column);
}
