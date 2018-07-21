package com.bc.jpa.paging;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * @author Josh
 * @param <T>
 */
public class ListPager<T> extends AbstractList<T> implements PaginatedList<T> {
    
    private final int batchSize;
    
    private final List<T> list;

    public ListPager(List<T> list, int batchSize) {
        this.list = Objects.requireNonNull(list);
//        if(batchSize > list.size()) {
//            throw new IllegalArgumentException();
//        }
        this.batchSize = batchSize;
    }

    @Override
    public void reset() { }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public int getSize() {
        return this.size();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public List<T> getPage(int pageNum) {
        int start = PagingUtil.getStart(pageNum, batchSize, size(), true, true);
        int end = PagingUtil.getEnd(pageNum, batchSize, size(), true, true);
//        System.out.println("Page Number: " + pageNum + ", start: " + start + ", end: " + end + ". @" + this.getClass());
        return list.subList(start, end);
     }

    @Override
    public final int getPageCount() {
        return PagingUtil.getBatchCount(this.getPageSize(), size());
    }

    @Override
    public final int getPageSize() {
        return batchSize;
    }
}
