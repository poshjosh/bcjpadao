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

/**
 * @author Chinomso Bassey Ikwuagwu on Jul 6, 2017 4:49:33 PM
 */
public abstract class AbstractSearchResults<T> extends AbstractPages<T> 
        implements Serializable, SearchResults<T> {

    private int pageNumber;
    
    private final boolean useCache;

    public AbstractSearchResults(int batchSize) {
        this(batchSize, true);
    }
    
    public AbstractSearchResults(int batchSize, boolean useCache) {
        super(batchSize);
        this.useCache = useCache;
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
    public final boolean isUseCache() {
        return this.useCache;
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
