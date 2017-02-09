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
 * Example class that wraps the execution of a {@link javax.persistence.TypedQuery} 
 * and paging the results using the provided {@linkplain #pageSize}
 * @param <T>
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public abstract class AbstractPages<T> implements Paginated<T> {

    /**
     * The size determined by {@link #calculateSize()}
     */
    private int $_size = -1;
    
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
        if(pageSize < 1) {
            throw new IllegalArgumentException("For page size, expected value > 0. found: "+pageSize);
        }
        this.batchSize = pageSize;
    }
    
    protected abstract List<T> loadBatch(int pageNum);
    
    protected abstract int calculateSize();
    
    @Override
    public void reset() {
        this.$_size = -1;
        this.previousPageNum = -1;
        this.batches = null;
    }
    
    @Override
    public int getSize() {
        if(this.$_size == -1) {
            this.$_size = calculateSize();
        }
        return this.$_size;
    }
    
    @Override
    public T get(int index) {
        
        int batchIndex = PagingUtil.getBatch(index, batchSize);
        int indexInBatch = PagingUtil.getIndexInBatch(index, batchSize);
        
        logFiner("Retreiving index {0}, batctCount: {1}, batchIndex: {2}, indexInBatch: {3}",
                index, this.getPageCount(), batchIndex, indexInBatch);
        
        return getPage(batchIndex).get(indexInBatch);
    }

    protected List<T> [] initPagesBuffer() {
        
        if(this.getPageSize() < 1) {
            throw new UnsupportedOperationException("Page size "+this.getPageSize()+"< 1");
        }
        
        final int numPages = this.computeNumberOfPages();
        
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, 
                "Size: {0}, number of pages: {1}", new Object[]{this.getSize(), numPages});
        
        //@numPages. null pages == not initialized, 0 pages == initialized but empty
        //
        List<T> [] pages = new List[numPages];

        return pages;
    }
    
    @Override
    public final int getPageSize() {
        return this.batchSize;
    }
    
    @Override
    public int getPageCount() {
        return getBatches().length;
    }

    public List<T>[] getBatches() {
        //@numPages. null pages == not initialized, 0 pages == initialized but empty
        //
        if(batches == null) {
            
            if(this.getPageSize() < 1) {
                throw new UnsupportedOperationException("Page size "+this.getPageSize()+" < 1");
            }
            
            batches = this.initPagesBuffer();

            logFiner( 
            "Initialized batches array. Total Size: {0}, batch size: {1}, number of batches: {2}", 
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
            
            page = this.loadBatch(pageNum);
            
            logFiner("Loaded from database. Batch {0}, size of batch: {1}",
                    pageNum, page == null ? null : page.size());
            
            getBatches()[pageNum] = page;
            
        }else{
            
            logFiner("Loaded from cache. Batch {0}, size of batch: {1}", pageNum, page.size());
        }
        
        if(!this.isUseCache() && previousPageNum > -1 && previousPageNum < this.getPageCount()) {
            
            logFiner("Clearing batch at {0}", previousPageNum);

            this.getBatches()[previousPageNum] = null;
            
            previousPageNum = pageNum;
        }

        return page;
    }
    
    protected final int computeNumberOfPages() {
        
        final int size = this.getSize();
        final int pageSize = this.getPageSize();
        
        //@numPages. null pages == not initialized, 0 pages == initialized but empty
        //
        return PagingUtil.getBatch(size, pageSize) + (PagingUtil.getIndexInBatch(size, pageSize) > 0 ? 1 : 0);
    }

    public boolean isUseCache() {
        return true;
    }

    public int getPreviousPage() {
        return previousPageNum;
    }
    
    private void logFiner(String fmt, Object arg) {
        Logger.getLogger(this.getClass().getName()).log(Level.FINER, fmt, arg);
    }
    
    private void logFiner(String fmt, Object arg0, Object arg1) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, fmt, new Object[]{arg0, arg1});
        }
    }

    private void logFiner(String fmt, Object arg0, Object arg1, Object arg2) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, fmt, new Object[]{arg0, arg1, arg2});
        }
    }
    
    private void logFiner(String fmt, Object arg0, Object arg1, Object arg2, Object arg3) {
        Logger logger = Logger.getLogger(this.getClass().getName());
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, fmt, new Object[]{arg0, arg1, arg2, arg3});
        }
    }
}
