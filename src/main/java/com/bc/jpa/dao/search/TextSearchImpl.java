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

package com.bc.jpa.dao.search;

import com.bc.jpa.dao.Criteria;
import com.bc.jpa.dao.JpaObjectFactory;
import com.bc.jpa.dao.Select;
import com.bc.jpa.dao.functions.GetColumnNamesOfType;
import com.bc.jpa.dao.util.EntityMemberAccess;
import com.bc.reflection.function.MethodIsGetter;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 28, 2017 1:58:39 PM
 */
public class TextSearchImpl implements TextSearch, Serializable {

    private transient static final Logger LOG = Logger.getLogger(TextSearchImpl.class.getName());
    
    private final JpaObjectFactory jpaObjectFactory;
    
    private final BiFunction<Class, Class, Collection<String>> columnNamesProvider;

    public TextSearchImpl(JpaObjectFactory jpaObjectFactory) {
        this(jpaObjectFactory, new GetColumnNamesOfType(jpaObjectFactory.getEntityManagerFactory()));
    }
    
    public TextSearchImpl(
            JpaObjectFactory jpaObjectFactory, 
            BiFunction<Class, Class, Collection<String>> columnNamesProvider) {
        this.jpaObjectFactory = Objects.requireNonNull(jpaObjectFactory);
        this.columnNamesProvider = Objects.requireNonNull(columnNamesProvider);
    }
    
    @Override
    public <T> List<T> search(Class<T> entityType, Object value, int minimumParts, 
            float factor, Function<Query, Query> queryFormatter) {
        
        final String text = this.countParts(value) > minimumParts 
                ? this.getSearchText(value, factor) : value.toString();
        
        return this.search(entityType, text, queryFormatter);
    }

    @Override
    public <T> List<T> search(Class<T> entityType, String text, 
            Criteria.ComparisonOperator comparisonOperator, Function<Query, Query> queryFormatter) {
        
        Objects.requireNonNull(entityType);
        Objects.requireNonNull(text);
        Objects.requireNonNull(comparisonOperator);
        
        final List<T> foundList;
        
        try(final Select<T> select = this.jpaObjectFactory.getDaoForSelect(entityType)) {

            final Collection<String> columnsToSearch = 
                    this.columnNamesProvider.apply(entityType, String.class);

            final Map<String, String> params;
            if(columnsToSearch.isEmpty()) {
                params = Collections.EMPTY_MAP;
            }else if(columnsToSearch.size() == 1) {
                params = Collections.singletonMap(columnsToSearch.iterator().next(), text);
            }else{
                params = new HashMap<>();
                for(String column : columnsToSearch) {
                    params.put(column, text);
                }
            }
            
            LOG.finer(() -> "Query: " + text + ", entity type: " + entityType + 
                    "\nColumns to search: " + columnsToSearch + "\nParameters: " + params);

            final TypedQuery<T> query = select
                    .from(entityType).where(comparisonOperator, Criteria.OR, params)
                    .createQuery();
                    
            foundList = queryFormatter.apply(query).getResultList();
        }    
        
        return foundList;
    }
    
    @Override
    public <T> List<T> search(Class<T> entityType, String text, 
            Function<Query, Query> queryFormatter) {
        
        Objects.requireNonNull(entityType);
        Objects.requireNonNull(text);
        
        final List<T> foundList;
        
        try(final Select<T> dao = this.jpaObjectFactory.getDaoForSelect(entityType)) {

            final Collection<String> columnsToSearch = 
                    this.columnNamesProvider.apply(entityType, String.class);
            
            LOG.finer(() -> "Query: " + text + ", entity type: " + entityType + 
                    "\nColumns to search: " + columnsToSearch);

            final TypedQuery<T> query = dao
                    .from(entityType).search(text.trim(), columnsToSearch)
                    .createQuery();
                    
            foundList = queryFormatter.apply(query).getResultList();
        }    
        
        return foundList;
    }

    @Override
    public boolean searchEntity(Object searchIn, Object searchFor, boolean textTypesOnly) {
        
        Objects.requireNonNull(searchIn);
        
        boolean found = false;
        
        final Method [] methods = searchIn.getClass().getMethods();
        
        final MethodIsGetter methodTest = new MethodIsGetter();
        
        for(Method method : methods) {
            
            if(methodTest.test(method)) {
                
                if(!textTypesOnly || method.getReturnType().equals(String.class)) {
                    
                    try{
                        
                        final Object methodValue = method.invoke(searchIn);

                        if(Objects.equals(methodValue, searchFor)) {
                            found = true;
                            break;
                        }
                    }catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            } 
        }
        
        return found;
    }
    
    @Override
    public <E> Collection<E> searchEntityRecords(E searchFor, Class... types) {
        
        final Map<String, Object> params = new LinkedHashMap<>();
        
        final Class<E> entityType = (Class<E>)searchFor.getClass();
        
        final EntityMemberAccess<E, Object> memberAccess = this.getEntityMemberAccess(entityType);
        
        for(Class type : types) {
            
            final Collection<String> colsOfType = this.columnNamesProvider.apply(entityType, type);
            
            for(String col : colsOfType) {
                
                final Object val = memberAccess.getValue(searchFor, col);
                if(val == null) {
                    params.putIfAbsent(col, val);
                }else{
                    params.put(col, val);
                }
            }
        }
        
        final Collection<E> output;
        
        if(params.isEmpty()) {
            output = Collections.EMPTY_LIST;
        }else{
            try(final Select<E> select = this.jpaObjectFactory.getDaoForSelect(entityType)) {
                select.from(entityType);
                for(String col : params.keySet()) {
                    final Object val = params.get(col);
                    select.where(col, val);
                }
                output = select.createQuery().getResultList();
            }
        }
        
        return output;
    }
    
    @Override
    public String getSearchText(Object value, float factor) {
        
        return reconstitute(value, factor);
    }
    
    public String reconstitute(Object value, float factor) {
        
        if(factor > 1.0f) {
            throw new IllegalArgumentException();
        }
        
        String sval = value.toString();
        
        if(factor < 1.0f) {
            
            final List<String> parts = this.getParts_internal(sval);

            sval = this.reconstitute(value, parts, factor);
        }
        
        return sval;
    }
    
    private String reconstitute(Object value, List<String> parts, float factor) {
        
        if(factor > 1.0f) {
            throw new IllegalArgumentException();
        }
        
        String sval = value.toString();
        
        if(factor < 1.0f) {
            
            if(parts.size() > 1) {
                
                final int n = (int)(parts.size() * factor); 

                if(n >= 0 && n < parts.size()) {
                    final String target = parts.get(n - 1);

                    final int off = sval.indexOf(target);

                    if(off != -1) {
                        final int end = off + target.length();
                        sval = sval.substring(0, end).trim();
                    }
                }
            }
        }
        
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "For factor: {0}, {1} formatted to {2}", new Object[]{factor, value, sval});
        }
        
        return sval;
    }

    @Override
    public List<String> getParts(Object value) {
        
        return Collections.unmodifiableList(this.getParts_internal(value));
    }

    private List<String> getParts_internal(Object value) {
        
        final String [] arr = this.split(value);
        
        final List<String> parts = new ArrayList(arr.length);
        Collections.addAll(parts, arr);

        final Iterator<String> iter = parts.iterator();
        
        while(iter.hasNext()) {
            
            final String part = iter.next();
            
            if(!this.testPart(part)) {
                
                iter.remove();
            }
        }

        LOG.finer(() -> "Value: " + value + ", parts: " + parts);
        
        return parts;
    }    

    public int countParts(Object value) {
        
        final String [] arr = this.split(value);
        
        int i = 0;
        
        for(String s : arr) {
            
            if(this.testPart(s)) {
                
                ++i;
            }
        }

        return i;
    }
    
    public String [] split(Object value) {
        
        final String sval = value.toString();
        
        final String [] arr = sval.split("\\s{1,}");
        
        return arr;
    }

    public boolean testPart(String s) {
        
        return s != null && !s.trim().isEmpty();
    }
    
    public <E> EntityMemberAccess<E, Object> getEntityMemberAccess(Class<E> entityType) {
        final EntityMemberAccess<E, Object> memberAccess = 
                this.jpaObjectFactory.getEntityMemberAccess(entityType);
        return memberAccess;
    } 

    public JpaObjectFactory getJpaObjectFactory() {
        return jpaObjectFactory;
    }

    public BiFunction<Class, Class, Collection<String>> getColumnNamesProvider() {
        return columnNamesProvider;
    }
}
