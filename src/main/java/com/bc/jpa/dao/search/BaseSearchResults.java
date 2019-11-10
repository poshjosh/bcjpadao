package com.bc.jpa.dao.search;

import com.bc.jpa.dao.SelectDao;

/**
 * @(#)ParamSearchResults.java   25-Apr-2015 22:45:02
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * This class must be closed after use via the {@link #close()} method
 * @param <R> The type of the result to return
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class BaseSearchResults<R> extends QuerySearchResults<R> implements AutoCloseable {
    
    private transient final SelectDao<R> select;
    
    public BaseSearchResults(SelectDao<R> select) {
        super(select.createQuery());
        this.select = select;
    }
    
    public BaseSearchResults(
            SelectDao<R> select, int batchSize, boolean useCache) { 
        super(select.createQuery(), batchSize, useCache);
        this.select = select;
    }
    
    public boolean isOpen() {
        return this.select != null && this.select.isOpen();
    }
    
    @Override
    public void close() {
        if(this.isOpen()) {
            this.select.close();
        }
    }

    @Override
    public R load(int index) {
        
        if(select != null) {

            final R entity = this.get(index);

            this.select.refresh(entity);
            
            return entity;
            
        }else{
            
            return super.load(index);
        }
    }

    public final SelectDao<R> getSelect() {
        return select;
    }
}
