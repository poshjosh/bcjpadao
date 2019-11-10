package com.bc.jpa.dao.sql;

/**
 * @(#)SQLDateTimePatterns.java   01-Jun-2013 15:35:14
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
@FunctionalInterface
public interface SQLDateTimePatterns {
    String getPattern(int sqlType);
}
