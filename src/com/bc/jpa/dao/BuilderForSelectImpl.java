package com.bc.jpa.dao;

import static com.bc.jpa.dao.Criteria.LIKE;
import static com.bc.jpa.dao.Criteria.OR;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

/**
 * @param <T>
 * @author Josh
 */
public class BuilderForSelectImpl<T> 
        extends BuilderForCriteriaDao<CriteriaQuery<T>, TypedQuery<T>, T, BuilderForSelect<T>> 
        implements BuilderForSelect<T> {
    
    private final CriteriaQuery criteriaQuery;
    
    private final Set<String> selectedColumns = new LinkedHashSet<>();
    
    public BuilderForSelectImpl(EntityManager em) {
        super(em);
        criteriaQuery = this.getCriteriaBuilder().createQuery();
    }
    
    public BuilderForSelectImpl(EntityManager em, Class<T> resultType) {
        super(em, resultType);
        criteriaQuery = this.getCriteriaBuilder().createQuery(resultType);
    }
    
    public BuilderForSelectImpl(EntityManager em, Class<T> resultType, DatabaseFormat databaseFormat) {
        super(em, resultType, databaseFormat);
        criteriaQuery = this.getCriteriaBuilder().createQuery(resultType);
    }

    @Override
    public CriteriaForSelect<T> getCriteria() {
        return this;
    }

    /**
     * Calls {@link #getSingleResult() getSingleResult()}, {@link #commit() commit()}
     * and {@link #close()}; in that order.
     * @return The single result
     */
    @Override
    public T getSingleResultAndClose() {
        try{
            if(!this.isBeginMethodCalled()) {
                this.begin();
            }
            final T result = this.getSingleResult();
            if(this.isBeginMethodCalled()) {
                this.commit();
            }        
            return result;
        }finally{
            this.close();
        }
    }

    /**
     * @see #getResultsAndClose(int, int) 
     * @return The List of results.
     */
    @Override
    public List<T> getResultsAndClose() {
        return this.getResultsAndClose(-1, -1);
    }
    
    /**
     * Calls {@link #getResultList() getResultList()}, {@link #commit() commit()}
     * and {@link #close() close()}; in that order.
     * @param firstResult Return results starting at this offset of the result list
     * @param maxResults  Return at most this number of results
     * @return The List of results
     */
    @Override
    public List<T> getResultsAndClose(int firstResult, int maxResults) {
        try{
            if(!this.isBeginMethodCalled()) {
                this.begin();
            }
            final List<T> result = this.getResultList(firstResult, maxResults);
            if(this.isBeginMethodCalled()) {
                this.commit();
            }        
            return result;
        }finally{
            this.close();
        }
    }

    public List<T> getResultList() {
        return this.getResultList(-1, -1);
    }

    public List<T> getResultList(int firstResult, int maxResults) {
        TypedQuery<T> tq = this.createQuery();
        if(firstResult > -1) {
            tq.setFirstResult(firstResult);
        }
        if(maxResults > -1) {
            tq.setMaxResults(maxResults);
        }
        return tq.getResultList();
    }
    
    public T getSingleResult() {
        return this.createQuery().getSingleResult();
    }

    @Override
    public T findAndClose(Object primaryKey) {
        return this.findAndClose(this.getResultType(), primaryKey);
    }
    @Override
    public T findAndClose(Object primaryKey, Map<String, Object> properties) {
        return this.findAndClose(this.getResultType(), primaryKey, properties);
    }
    @Override
    public T findAndClose(Object primaryKey, LockModeType lockMode) {
        return this.findAndClose(this.getResultType(), primaryKey, lockMode);
    }
    @Override
    public T findAndClose(Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        return this.findAndClose(this.getResultType(), primaryKey, lockMode, properties);
    }
    
    @Override
    protected Root doFrom(Class entityClass) {
        return this.criteriaQuery.from(entityClass);
    }

    @Override
    protected CriteriaQuery<T> doBuild() {
        Predicate restriction = this.getRestriction();
        if(restriction != null) {
            criteriaQuery.where(restriction);
        }
        List<Order> orders = this.getOrders();
        if(orders != null && !orders.isEmpty()) {
            criteriaQuery.orderBy(orders);
        }
        return criteriaQuery;
    }
    
    @Override
    protected TypedQuery<T> doCreateQuery(CriteriaQuery<T> q) {
        return this.getEntityManager().createQuery(q);
    }
    
    @Override
    public BuilderForSelect search(String query, Collection<String> cols) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.search(getCurrentEntityType(), query, cols);
    }
    
    @Override
    public BuilderForSelect search(Class entityType, String query, Collection<String> cols) {
        
        return search(entityType, query, cols.toArray(new String[0]));
    }

    @Override
    public BuilderForSelect search(String query, String... cols) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.search(getCurrentEntityType(), query, cols);
    }
    
    @Override
    public BuilderForSelect search(Class entityType, String query, String... cols) {
        
        this.throwExceptionIfBuilt();
        
        if(query == null || cols == null) {
            throw new NullPointerException();
        }
        
        if(cols.length == 0) {
            throw new UnsupportedOperationException("Attempting to search through an empty list of columns");
        }
        
        final String TEXT_TO_FIND = '%' + query + '%';
        
        for(String col:cols) {
            
            this.where(entityType, col, LIKE, TEXT_TO_FIND, OR);
        }
        
        return this;
    }

    @Override
    public BuilderForSelect select(Collection<String> cols) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.select(getCurrentEntityType(), cols);
    }
    
    @Override
    public BuilderForSelect select(Class fromEntityType, Collection<String> cols) {
        this.throwExceptionIfBuilt();
        return this.select(fromEntityType, cols.toArray(new String[0]));
    }

    @Override
    public BuilderForSelect select(String... cols) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.select(getCurrentEntityType(), cols);
    }
    
    @Override
    public BuilderForSelect select(Class fromEntityType, String... cols) {
        
        this.throwExceptionIfBuilt();
        
        if(fromEntityType == null || cols == null) {
            throw new NullPointerException();
        }
        
        if(cols.length == 0) {
            throw new UnsupportedOperationException("Attempting to select an empty list of columns");
        }
        
        return this.doSelect(fromEntityType, cols);
    }

    @Override
    public BuilderForSelect<T> sum(Collection<String> cols) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.sum(getCurrentEntityType(), cols);
    }
    
    @Override
    public BuilderForSelect<T> sum(Class entityType, Collection<String> cols) {
        this.throwExceptionIfBuilt();
        return this.sum(entityType, cols.toArray(new String[0]));
    }

    @Override
    public BuilderForSelect<T> sum(String... cols) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.sum(getCurrentEntityType(), cols);
    }
    
    @Override
    public BuilderForSelect<T> sum(Class entityType, String... cols) {
        
        this.throwExceptionIfBuilt();
        
        if(entityType == null || cols == null) {
            throw new NullPointerException();
        }
        
        if(cols.length == 0) {
            throw new UnsupportedOperationException("Attempting to select an empty list of columns");
        }
        
        return this.doSum(entityType, cols);
    }
    
    @Override
    public BuilderForSelect<T> count() {
        this.throwExceptionIfNullCurrentEntityType();
        return this.count(getCurrentEntityType());
    }
    
    @Override
    public BuilderForSelect<T> count(Class entityType) {
        Objects.requireNonNull(entityType);
        return this.doCount(entityType);
    }
    
    @Override
    public BuilderForSelect<T> count(String col) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.count(getCurrentEntityType(), col);
    }

    @Override
    public BuilderForSelect<T> count(Class entityType, String col) {
        
        this.throwExceptionIfBuilt();
        
        if(entityType == null || col == null) {
            throw new NullPointerException();
        }
        
        return this.doCount(entityType, col);
    }

    @Override
    public BuilderForSelect<T> max(String col) {
        
        this.throwExceptionIfNullCurrentEntityType();
        
        return this.max(getCurrentEntityType(), col);
    }
    
    @Override
    public BuilderForSelect<T> max(Class entityType, String col) {
        
        this.throwExceptionIfBuilt();
        
        if(entityType == null || col == null) {
            throw new NullPointerException();
        }
        
        return this.doMax(entityType, col);
    }
    
    @Override
    public BuilderForSelect distinct(boolean b) {
    
        this.throwExceptionIfBuilt();
        
        this.criteriaQuery.distinct(b); 
        
        return this;
    }

    protected BuilderForSelect doSelect(Class fromEntityType, String... cols) {
        
        From root = this.from(fromEntityType, true);

        if(cols.length == 1) {
            final Selection curr = this.getPath(root, cols[0]);
            final Selection prev = criteriaQuery.getSelection();
            if(prev == null) {
                criteriaQuery.select(curr);
            }else{
                if(prev.isCompoundSelection()) {
                    List list = prev.getCompoundSelectionItems();
                    list.add(curr);
                    criteriaQuery.multiselect(list);
                }else{
                    criteriaQuery.multiselect(prev, curr);
                }
            }
            selectedColumns.add(cols[0]);
        }else{
            final Selection [] currArr = this.getPaths(root, cols);
            final Selection prev = criteriaQuery.getSelection();
            if(prev == null) {
                criteriaQuery.multiselect(currArr);
            }else{
                if(prev.isCompoundSelection()) {
                    final List list = prev.getCompoundSelectionItems();
                    list.addAll(Arrays.asList(currArr));
                    criteriaQuery.multiselect(list);
                }else{
                    final Selection [] updateArr = new Selection[currArr.length + 1];
                    updateArr[0] = prev;
                    System.arraycopy(currArr, 0, updateArr, 1, currArr.length);
                    criteriaQuery.multiselect(updateArr);
                }
            }
            selectedColumns.addAll(Arrays.asList(cols));
        }
        
        return this;
    }

    @Override
    public Set<String> getSelectedColumns() {
        return Collections.unmodifiableSet(selectedColumns);
    }

    protected BuilderForSelect doSum(Class entityType, String... cols) {
        
        From root = this.from(entityType, true);
        
        CriteriaBuilder criteriaBuilder = getCriteriaBuilder();
        
        if(cols.length == 1) {
            final Path path = this.getPath(root, cols[0]);
            Expression sumExpr = criteriaBuilder.sum(path);
            criteriaQuery.select(sumExpr);
        }else{
            final Path [] paths = this.getPaths(root, cols);
            final Expression [] sumExprArr = new Expression[paths.length];
            int offset = 0;
            for(Path path:paths) {
                sumExprArr[offset++] = criteriaBuilder.sum(path);
            }
            criteriaQuery.multiselect(sumExprArr);
        }
        
        return this;
    }

    protected BuilderForSelect doMax(Class entityType, String col) {
        From root = this.from(entityType, true);
        Path path = this.getPath(root, col);
        Expression countExpr = getCriteriaBuilder().max(path); 
        criteriaQuery.select(countExpr);
        return this;
    }

    protected BuilderForSelect doCount(Class entityType) {
        From root = this.from(entityType, true);
        Expression countExpr = getCriteriaBuilder().count(root);
        criteriaQuery.select(countExpr);
        return this;
    }

    protected BuilderForSelect doCount(Class entityType, String col) {
        From root = this.from(entityType, true);
        Path path = this.getPath(root, col);
        Expression countExpr = getCriteriaBuilder().count(path);
        criteriaQuery.select(countExpr);
        return this;
    }

    @Override
    public final CriteriaQuery getCriteriaQuery() {
        return criteriaQuery;
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() +  "{\n  built=" + this.isBuilt() + ", resultType=" + this.getResultType() + ", where=" + getRestriction() + ", nextConnector=" + getNextConnector() + "\n  roots=" + getRoots() + '}';// + "\n  joins=" + joins + '}';
    }
}