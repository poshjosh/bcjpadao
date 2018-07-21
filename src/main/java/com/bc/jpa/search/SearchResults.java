package com.bc.jpa.search;

import java.util.List;
import com.bc.jpa.paging.Paginated;
import com.bc.jpa.paging.PaginatedList;
import java.io.Serializable;
import java.util.Collections;


/**
 * @(#)SearchResults.java   14-Apr-2015 21:47:30
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @param <T>
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public interface SearchResults<T> extends Paginated<T> {
    
    SearchResults EMPTY_INSTANCE = new EmptySearchResults();
    
    List<T> loadPage(int pageNum);
    
    default boolean load(T result) {
        final int index = this.getPages().indexOf(result);
        return index == -1 ? false : this.load(index) != null;
    }
    
    T load(int index);
    
    PaginatedList<T> getPages();
    
    List<T> getCurrentPage();

    List<T> getPage();

    int getPageNumber();

    @Override
    int getSize();

    void setPageNumber(int pageNumber);
    
    class EmptySearchResults<T> extends EmptyPages<T> 
            implements SearchResults<T>, Serializable {

        @Override
        public List<T> loadPage(int pageNum) { 
            return Collections.EMPTY_LIST;
        }

        @Override
        public T load(int index) { 
            throw new IndexOutOfBoundsException("0 elements available. Index out of bounds: "+index);
        }

        @Override
        public PaginatedList<T> getPages() {
            return PaginatedList.EMPTY_PAGINATED_LIST;
        }

        @Override
        public List getCurrentPage() {
            return this.getPage();
        }

        @Override
        public List getPage() {
            return this.getPage(this.getPageNumber());
        }

        @Override
        public int getPageNumber() {
            return 0;
        }

        @Override
        public void setPageNumber(int pageNumber) {
            throw new IndexOutOfBoundsException("0 elements available. Index out of bounds. Page: "+pageNumber);
        }
    }
}
