package com.bc.jpa.dao.util;

import com.bc.jpa.dao.JpaObjectFactory;
import com.bc.jpa.dao.functions.ConvertToEntityFieldNumberType;
import com.bc.jpa.dao.functions.GetColumnNames;
import com.bc.jpa.dao.sql.SQLDateTimePatterns;
import com.bc.jpa.dao.sql.SQLUtils;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.JoinColumn;

/**
 * @(#)DatabaseValue.java   18-Apr-2015 04:49:47
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
public class DatabaseFormatImpl implements DatabaseFormat {

    private transient static final Logger LOG = Logger.getLogger(DatabaseFormatImpl.class.getName());

    private final SQLDateTimePatterns dateTimePatterns;
    
    private final EntityReference entityReference;
    
    private final JpaObjectFactory jpaContext;

    private final ConvertToEntityFieldNumberType toNumber;
    
    private final Function<Class, List<String>> getColumnNames;
    
    public DatabaseFormatImpl(JpaObjectFactory jpaContext, SQLDateTimePatterns dateTimePatterns) { 
        this.dateTimePatterns = Objects.requireNonNull(dateTimePatterns);
        this.jpaContext = Objects.requireNonNull(jpaContext);
        this.entityReference = Objects.requireNonNull(jpaContext.getEntityReference());
        this.toNumber = new ConvertToEntityFieldNumberType();
        this.getColumnNames = new GetColumnNames(jpaContext.getEntityManagerFactory());
    }

    public final SQLDateTimePatterns getDateTimePatterns() {
        return dateTimePatterns;
    }

    @Override
    public Object toDatabaseFormat(Class entityType, Object key, Object value, Object outputIfNone) {
        
        if(LOG.isLoggable(Level.FINEST)) {
            LOG.log(Level.FINEST, "{0}#{1} = {2}", new Object[]{entityType.getSimpleName(), key, value});
        }
        Object output;

        if(!this.isDatabaseColumn(entityType, key)) {
            
            output = outputIfNone;
            
        }else if(value == null) {

//@ todo This may not be suitable for creating queries, in which case 'NULL' may be suitable
            output = null; 
            
        }else if(value.equals("")) {
            
            output = value;
            
        }else{

            Object sqlVal = this.getSQLValue(entityType, key, value);
            
            output = sqlVal != null ? sqlVal : value;
            
            // Replace the value with the reference if possible
            //
            Optional optionalRef = this.getReferenceOptional(entityType, key, output);

            if(optionalRef.isPresent()) {

                output = optionalRef.get();
            }
        }
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "For: {0}#{1}, converted: {2} to {3}", 
                    new Object[]{entityType==null?null:entityType.getName(), key, value, output});
        }
        return output;
    }
    
    public Object getSQLValue(Class entityType, Object key, Object value) {
        Object sqlObj = null;
        try{

            final Field field = entityType.getDeclaredField(key.toString());
            if(field != null) {
                final Class fieldType = field.getType();
                int [] sqlTypes = SQLUtils.getTypes(fieldType);
                if(sqlTypes != null && sqlTypes.length > 0) {

                    if(LOG.isLoggable(Level.FINER)) {
                        LOG.log(Level.FINER, "Key: {0}, value type: {1}, value: {2}", 
                                new Object[]{key, value==null?null:value.getClass().getName(), value});
                    }
                    
                    final int sqlType = sqlTypes[0];
    
                    if(SQLUtils.isDateOrTimeType(sqlType)) {
                        sqlObj = SQLUtils.toSQLType(dateTimePatterns, sqlType, value);
                    }

                    if(LOG.isLoggable(Level.FINER)) {
                        LOG.log(Level.FINER, "Converted: {0} to SQL value: {1}", 
                                new Object[]{value, sqlObj});
                    }
                }
                
                final JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                if(joinColumn != null) {
                    if(LOG.isLoggable(Level.FINER)) {
                        LOG.log(Level.FINER, "Entity class: {0}, column: {1} join column: {2}", 
                                new Object[]{entityType, key, joinColumn});
                    }
                    
                    final Object number = this.toNumber.apply(entityType, key.toString(), value, null);
                    if(number != null) {
                        sqlObj = number;
                        if(LOG.isLoggable(Level.FINER)) {
                            LOG.log(Level.FINER, "Converted: {0} to SQL value: {1} of type: {2}", 
                                    new Object[]{
                                        value, 
                                        sqlObj, 
                                        sqlObj==null?null:sqlObj.getClass()
                                    }
                            );
                        }
                    }
                }
            }
        }catch(NoSuchFieldException | RuntimeException e) {
            LOG.log(Level.WARNING, "Error converting '"+key+"' to sql type", e);
        }
        
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "For: {0}#{1}, converted: {2} to {3} of type: {4}", 
                    new Object[]{entityType==null?null:entityType.getName(), key, value, sqlObj, sqlObj==null?null:sqlObj.getClass()});
        }
        return sqlObj;
    }
    
    @Override
    public boolean isDatabaseColumn(Class entityType, Object key) {
        
        final List<String> cols = this.getColumnNames(entityType);
        
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "Entity type: {0}, columns: {1}", 
                    new Object[]{entityType.getSimpleName(), cols});
        }
        
        final String col = key.toString();
        
        final boolean output = cols.contains(col);
        
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "Is database column: {0}, for {1}#{2}", 
                    new Object[]{output, entityType.getSimpleName(), col});
        }

        return output;
    }
    
    public List<String> getColumnNames(Class entityType) {
        return this.getColumnNames.apply(entityType);
    }
    
    public Optional getReferenceOptional(Class entityType, Object key, Object value) {
        
        if(LOG.isLoggable(Level.FINEST)) {
            LOG.log(Level.FINEST, "{0}#{1} = {2}", 
                    new Object[]{entityType.getSimpleName(), key, value});
        }

        final String col = key.toString();
        
        final Optional optionalRef = entityReference.getReferenceOptional(entityType, col, value);

        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "Found ref: {0}, for {1}#{2} = {3}", 
                    new Object[]{optionalRef.orElse(null), entityType.getSimpleName(), col, value});
        }
        
        return optionalRef;
    }

    public JpaObjectFactory getJpaContext() {
        return jpaContext;
    }
}
