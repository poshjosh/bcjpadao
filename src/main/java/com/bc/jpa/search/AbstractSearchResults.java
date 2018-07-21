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

package com.bc.jpa.search;

import com.bc.jpa.paging.AbstractPages;
import com.bc.jpa.paging.PaginatedList;
import com.bc.jpa.paging.PaginatedListImpl;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 6, 2017 4:49:33 PM
 */
public abstract class AbstractSearchResults<T> extends AbstractPages<T> 
        implements Serializable, SearchResults<T> {

    private transient static final Logger LOG = Logger.getLogger(AbstractSearchResults.class.getName());

    private int pageNumber;
    
    private PaginatedList pages;
    
    public AbstractSearchResults(int batchSize) {
        super(batchSize, true);
    }
    
    public AbstractSearchResults(int batchSize, boolean useCache) {
        super(batchSize, useCache);
    }

    /**
     * This sets the current results to null and eventually causes a fresh set 
     * of results to be re-loaded from the database. Size will be re-calculated.
     * Pagination starts from the first page.
     */
    @Override
    public void reset() {
        super.reset(); 
        this.setPageNumber(0);
    }
    
    @Override
    public PaginatedList<T> getPages() {
        if(pages == null) {
            pages = new PaginatedListImpl(this);
        }
        return pages;
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
        LOG.fine(() -> "Updating page from: " + this.pageNumber + ", to: " + pageNumber);
        this.pageNumber = pageNumber;
    }
}
