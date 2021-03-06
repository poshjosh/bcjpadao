package com.bc.jpa.dao.search;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;

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
public class QuerySearchResults<T> extends AbstractSearchResults<T> {

    private transient static final Logger LOG = Logger.getLogger(QuerySearchResults.class.getName());

    private transient final Query query;
    
    public QuerySearchResults(Query query) {
        this(query, 20);
    }
    
    public QuerySearchResults(Query query, int batchSize) {
        this(query, batchSize, true);
    }

    public QuerySearchResults(Query query, int batchSize, boolean useCache) {
        super(batchSize, useCache);
        this.query = Objects.requireNonNull(query);
    }

    @Override
    public List<T> loadPage(int pageNum) {
        final Map<String, Object> hints = query.getHints();
        final Object refresh = (hints == null) ? null : hints.get(QueryHints.REFRESH);
        try{
            query.setHint(QueryHints.REFRESH, HintValues.TRUE);
            final int batchSize = this.getPageSize();
            query.setFirstResult(batchSize * pageNum);
            query.setMaxResults(batchSize);
            return query.getResultList();
        }finally{
            if(refresh == null) {
                query.getHints().remove(QueryHints.REFRESH);
            }else{
                query.setHint(QueryHints.REFRESH, refresh);
            }
        }
    }
    
    /**
     * <p>
     * Using the provided {@link TypedQuery} to calculate the size. The query is
     * copied to create a new query which just retrieves the count.
     * </p>
     * <b>Notes:</b>
     * <ul>
     * <li>The query should contain an ORDER BY</li> 
     * <li>The following methods must not have been called on the query:<br/>
     * {@link javax.persistence.TypedQuery#setFirstResult(int)}<br/> 
     * {@link javax.persistence.TypedQuery#setMaxResults(int)}
     * </li>
     * </ul>
     * @return 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected int calculateSize() {
        LOG.finer(() -> "Calculating size");
        if(query == null) {
            throw new NullPointerException();
        }
        
        JpaQuery<T> queryImpl = (JpaQuery<T>)query;
        ReadAllQuery raq = JpaHelper.getReadAllQuery(query);

        ReportQuery rq;

        if (raq.isReportQuery()) {
            rq = (ReportQuery) raq.clone();
            rq.getItems().clear();
            rq.addCount();
            rq.getGroupByExpressions().clear();
            rq.getOrderByExpressions().clear();
        } else {
            rq = new ReportQuery();
            rq.setReferenceClass(raq.getReferenceClass());
            rq.addCount();
            rq.setShouldReturnSingleValue(true);
            rq.setSelectionCriteria(raq.getSelectionCriteria());
        }

        if(raq.isDistinctComputed()) {
            rq.setDistinctState(raq.getDistinctState());
        }
        
        final EntityManager entityManager = queryImpl.getEntityManager();
        
        // Wrap new report query as JPA query for execution with parameters
        final TypedQuery<Number> countQuery = (TypedQuery<Number>) JpaHelper.createQuery(rq, entityManager);

        LOG.finer(() -> "Done creating count query");

        try{
// Temporary solution = Rather than use Query of type: 'SELECT p FROM User p', explicitly name the columns of the entity this way: 'SELECT p.name, p.age, p.height...etc FROM Person p'
            
//This first line is used in the bug title below... all changes be reflected below            
//@bug QueryResultPages#calculateSize(TypedQuery) TypedQuery.getParameters throws NullpointerException            
//query.getParameters() often throws the below exception
//java.lang.NullPointerException
//	at org.eclipse.persistence.internal.jpa.EJBQueryImpl.getParameters(EJBQueryImpl.java:1442)
//	at com.loosedb.pu.jpa.QueryResultPages.calculateSize(QueryResultPages.java:149)
//	at com.loosedb.pu.jpa.QueryResultPages.<init>(QueryResultPages.java:72)
//	at com.loosedb.pu.jpa.PagingListTest.testAll(PagingListTest.java:89)
            Set<Parameter<?>>  params;
            if((params = query.getParameters()) != null) {
                
                LOG.log(Level.FINER, "Query parameters: {0}", params);
                
                // Copy parameters
                for (Parameter param : params) {
                    countQuery.setParameter(param, query.getParameterValue(param));
                }
            }
        }catch(RuntimeException bug) { 
            StringBuilder builder = new StringBuilder();
            builder.append("This is a bug. Search for '@bug QueryResultPages' to locate.\n");
            builder.append("Temporary solution = Rather than use Query of type: 'SELECT p FROM User p', explicitly name the columns of the entity this way: 'SELECT p.name, p.age, p.height...etc FROM Person p'");
            LOG.log(Level.WARNING, builder.toString(), bug);
        }

        final int size = countQuery.getSingleResult().intValue();
        
        LOG.fine(() -> "Size: " + size);
        
        return size;
    }

    public final Query getQuery() {
        return query;
    }
}
