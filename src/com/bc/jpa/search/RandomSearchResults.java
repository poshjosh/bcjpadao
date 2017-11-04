package com.bc.jpa.search;

import com.bc.jpa.paging.ListPager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import com.bc.jpa.paging.PaginatedList;
import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Logger;


/**
 * @(#)RandomSearchResults.java   31-May-2015 00:26:59
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
public class RandomSearchResults<T> implements SearchResults<T>, Serializable {

    private final List<T> results;
    
    private final int pageSize;
    
    public RandomSearchResults(List<T> results) {
        this(results, 20);
    }
    
    public RandomSearchResults(List<T> results, int pageSize) {
        this.results = Objects.requireNonNull(results);
        this.pageSize = pageSize;
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, 
                "Random Search Results input size: {0}", results.size());
    }
    
    @Override
    public void reset() { }

    @Override
    public List<T> loadPage(int pageNum) {
        return this.getPage(pageNum);
    }

    @Override
    public T load(int index) {
        return this.get(index);
    }

    @Override
    public T get(int index) {
        return results.get(index);
    }

    @Override
    public PaginatedList<T> getPages() {
        return new ListPager(results, this.getPageSize());
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
    public int getSize() {
        return results == null ? 0 : results.size();
    }

    @Override
    public void setPageNumber(int pageNumber) { }

    @Override
    public List getPage(int pageNum) {
        final int totalSize = this.getSize();
        if(totalSize == 0 || totalSize <= this.getPageSize()) {
            return results;
        }else{
            final int batchSize = totalSize < this.getPageSize() ? totalSize : this.getPageSize();
            List<T> batch = new ArrayList<>(batchSize);
            while(batch.size() < batchSize) {
                int index = this.randomInt(totalSize);
                T t = results.get(index);
                boolean added = batch.contains(t);
                if(!added) {
                    batch.add(t);
                }
            }
            
            Logger.getLogger(this.getClass().getName()).log(Level.FINER, "Random results:: {0}", batch);
            
            return batch;
        }
    }

    @Override
    public int getPageCount() {
        return results == null ? 0 : 1;
    }

    @Override
    public int getPageSize() {
        return this.pageSize;
    }

    /**
     * @param size The returned int will be of range: 0 - <tt>size</tt>
     * @return  a pseudorandom <code>int</code> greater than or equal 
     * to <code>0</code> and less than the input<code>size</code>.
     * @see     com.bc.util.Util#random(double) 
     * @see     java.lang.Math#random()
     */
    public int randomInt(int size) {
        
        double numbr = random(size);

        return (int)Math.floor(numbr);
    }
    
    /**
     * Returns a <code>double</code> value with a positive sign, greater 
     * than or equal to <code>0</code> and less than input <code>size</code>. 
     * Returned values are chosen pseudo-randomly with (approximately) 
     * uniform distribution from that range. 
     * 
     * <p>When this method is first called, it creates a single new
     * pseudorandom-number generator, exactly as if by the expression
     * <blockquote><pre>new java.util.Random</pre></blockquote> This
     * new pseudorandom-number generator is used thereafter for all
     * calls to this method and is used nowhere else.
     * 
     * <p>This method is properly synchronized to allow correct use by
     * more than one thread. However, if many threads need to generate
     * pseudorandom numbers at a great rate, it may reduce contention
     * for each thread to have its own pseudorandom-number generator.
     *  
     * @param size The returned double will be of range: 0 - <tt>size</tt>
     * @return  a pseudorandom <code>double</code> greater than or equal 
     * to <code>0</code> and less than the input<code>size</code>.
     * @see     java.lang.Math#random()
     */
    public double random(double size) {
        
        double random = Math.random();

        double numbr = (random * size);

        return numbr;
    }
}
