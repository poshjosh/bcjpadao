package com.bc.jpa.search;

import java.io.Serializable;
import java.util.List;
import com.bc.jpa.paging.PaginatedList;
import com.bc.jpa.paging.PaginatedListImpl;
import javax.persistence.Query;

/**
 * @(#)AbstractSearchResults.java   11-Apr-2015 08:28:48
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */
/**
 * <p>
 * Example class that wraps the execution of a {@link javax.persistence.TypedQuery} 
 * calculating the current size and then paging the results using the provided 
 * page size.
 * </p>
 * <b>Notes:</b>
 * <ul>
 * <li>The query should contain an ORDER BY</li> 
 * <li>The following methods must not have been called on the query:<br/>
 * {@link javax.persistence.TypedQuery#setFirstResult(int)}<br/> 
 * {@link javax.persistence.TypedQuery#setMaxResults(int)}
 * </li>
 * <li>The usage of this may produce incorrect results if the matching data set 
 * changes on the database while the results are being paged.</li>
 * </ul>
 * @param <T>
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public class QuerySearchResultsOld<T> 
        extends QueryResultPages<T>
        implements Serializable, SearchResults<T> {

    private int pageNumber;
    
    public QuerySearchResultsOld(Query query) { 
        this(query, 20);
    }

    public QuerySearchResultsOld(Query query, int batchSize) {
        this(query, batchSize, true);
    }
    
    public QuerySearchResultsOld(Query query, int batchSize, boolean useCache) {
        super(query, batchSize, useCache);
    }

    @Override
    public void reset() {
        super.reset(); 
        this.pageNumber = 0;
    }
    
    @Override
    public PaginatedList<T> getPages() {
        return new PaginatedListImpl(this);
    }
    
    @Override
    public List<T> getPage() {
        return getCurrentPage();
    }
    
    @Override
    public List<T> getCurrentPage() {
        return getPage(pageNumber);
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public void setPageNumber(int pageNumber) {
        if(pageNumber > this.getPageCount()-1) {
            throw new ArrayIndexOutOfBoundsException(pageNumber+" >= "+this.getPageCount());
        }
        this.pageNumber = pageNumber;
    }
}
