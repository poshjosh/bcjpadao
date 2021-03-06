package com.bc.jpa.paging;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @(#)AbstractPagingList.java   27-Jun-2014 13:36:14
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
public abstract class AbstractPages<T> implements Paginated<T> {
    
    private transient static final Logger logger = Logger.getLogger(AbstractPages.class.getName());
    
    /**
     * The size determined by {@link #calculateSize()}
     */
    private int $_size = -1;
    
    private final boolean useCache;
    
    private final int batchSize;
    
    private int previousPageNum = -1;

    /**
     * Cached page results. This approach holds all retrieved pages. if the
     * total volume of results is large this caching should be changed to only
     * hold a limited set of pages or rely on garbage collection to clear out
     * unused pages.
     */
    private List<T>[] batches;
    
    public AbstractPages(int pageSize) {  
        this(pageSize, true);
    }
    
    public AbstractPages(int pageSize, boolean useCache) {  
        if(pageSize < 1) {
            throw new IllegalArgumentException("For page size, expected value > 0. found: "+pageSize);
        }
        this.useCache = useCache;
        this.batchSize = pageSize;
        logger.log(Level.FINE, "Page size: {0}", pageSize);
    }
    
    protected abstract int calculateSize();
    
    public abstract List<T> loadPage(int pageNum);

    public T load(int index) {
        
        return this.loadPage(this.getPageNumber(index)).get(this.getIndexInPage(index));
    }
    /**
     * This sets the current results to null and eventually causes a fresh set 
     * of results to be re-loaded from the database. Size will be re-calculated
     */
    @Override
    public void reset() {
        this.$_size = -1;
        if(this.batches != null) {
            this.batches = null;
        }
        this.previousPageNum = -1;
    }
    
    @Override
    public int getSize() {
        if(this.$_size == -1) {
            this.$_size = this.calculateSize();
        }
        return this.$_size;
    }
    
    @Override
    public T get(int index) {
        
        final int batchIndex = this.getPageNumber(index);
        final int indexInBatch = this.getIndexInPage(index);
        
        log(Level.FINER, "Retreiving index {0}, pageCount: {1}, pageIndex: {2}, indexInPage: {3}",
                index, this.getPageCount(), batchIndex, indexInBatch);
        
        return getPage(batchIndex).get(indexInBatch);
    }

    protected List<T> [] initPagesBuffer() {
        
        if(this.getPageSize() < 1) {
            throw new UnsupportedOperationException("Page size "+this.getPageSize()+"< 1");
        }
        
        final int numPages = this.computeNumberOfPages();
        
        log(Level.FINE, 
                "Size: {0}, number of pages: {1}", this.getSize(), numPages);
        
        //@numPages. null pages == not initialized, 0 pages == initialized but empty
        //
        List<T> [] pages = new List[numPages];

        return pages;
    }
    
    public List<T>[] getBatches() {
        //@numPages. null pages == not initialized, 0 pages == initialized but empty
        //
        if(batches == null) {
            
            if(this.getPageSize() < 1) {
                throw new UnsupportedOperationException("Page size "+this.getPageSize()+" < 1");
            }
            
            batches = this.initPagesBuffer();

            log(Level.FINE, 
            "Initialized pages array. Total Size: {0}, page size: {1}, number of pages: {2}", 
            this.getSize(), this.getPageSize(), this.getPageCount());

        }
        return batches;
    }
    
    @Override
    public List<T> getPage(int pageNum) {
        
        if(pageNum < 0 || pageNum >= this.getPageCount()) {
            throw new IndexOutOfBoundsException("Page number: "+pageNum+", number of pages: "+this.getPageCount());
        }
        
        List<T> page = getBatches()[pageNum];

        if (page == null) {
            
            page = this.loadPage(pageNum);
            
            log(Level.FINER, "Loaded from database. Page number {0}, size of page: {1}",
                    pageNum, page == null ? null : page.size());
            
            getBatches()[pageNum] = page;
            
        }else{
            
            log(Level.FINER, "Loaded from cache. Page number: {0}, size of page: {1}", pageNum, page.size());
        }
        
        if(!this.isUseCache() && previousPageNum > -1 && previousPageNum < this.getPageCount()) {
            
            logger.log(Level.FINE, "Clearing page at {0}", previousPageNum);

            this.getBatches()[previousPageNum] = null;
            
            previousPageNum = pageNum;
        }

        return page;
    }
    
    protected final int computeNumberOfPages() {
        
        final int size = this.getSize();
        
        //@numPages. null pages == not initialized, 0 pages == initialized but empty
        //
        return this.getPageNumber(size) + (this.getIndexInPage(size) > 0 ? 1 : 0);
    }

    public final boolean isUseCache() {
        return this.useCache;
    }

    @Override
    public final int getPageSize() {
        return this.batchSize;
    }
    
    @Override
    public int getPageCount() {
        return getBatches().length;
    }

    private void log(Level level, String fmt, Object arg0, Object arg1) {
        if(logger.isLoggable(level)) {
            logger.log(level, fmt, new Object[]{arg0, arg1});
        }
    }

    private void log(Level level, String fmt, Object arg0, Object arg1, Object arg2) {
        if(logger.isLoggable(level)) {
            logger.log(level, fmt, new Object[]{arg0, arg1, arg2});
        }
    }
    
    private void log(Level level, String fmt, Object arg0, Object arg1, Object arg2, Object arg3) {
        if(logger.isLoggable(level)) {
            logger.log(level, fmt, new Object[]{arg0, arg1, arg2, arg3});
        }
    }
}
