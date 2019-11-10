package com.bc.jpa.dao;

import com.bc.jpa.dao.util.DatabaseFormat;
import static com.bc.jpa.dao.Criteria.AND;
import static com.bc.jpa.dao.Criteria.OR;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 17, 2016 6:21:58 PM
 * @param <C> The type of output produced by the {@link #build() builder()}  
 * e.g CriteriaQuery, CriteriaDelete, CriteriaUpdate.
 * @param <Q> The type of {@link javax.persistence.Query Query} returned by this
 * {@link #createQuery() createQuery()} method.
 * @param <T> The type of the entity class
 * @param <D> The type of the {@link com.bc.jpa.dao.CriteriaDao CriteriaDao} returned by methods in this class
 */
public abstract class BuilderForCriteriaDao<C extends CommonAbstractCriteria, Q extends Query, T, D extends CriteriaDao<C, Q, T, D>>
        extends DaoImpl
        implements Criteria<C, Q, T, D>, 
        CriteriaDao<C, Q, T, D> {

    private transient static final Logger logger = Logger.getLogger(BuilderForCriteriaDao.class.getName());
    
    private boolean built;
    
    private Class currentEntityType;
    
    private Class joinFromType;

    private Class joinToType;
    
    private From currentFrom;
    
    private Join join;
    
    private Predicate restriction;
    
    private Criteria.LogicalOperator nextConnector;
    
    private CriteriaBuilder criteriaBuilder;
    
    private final Map<Class, Root> roots;
    
    private List<Order> orders;
    
    public BuilderForCriteriaDao(EntityManager em) {
        this(em,  null);
    }
    
    public BuilderForCriteriaDao(EntityManager em, DatabaseFormat databaseFormat) {
        
        super(em, databaseFormat);
        
        this.criteriaBuilder = em.getCriteriaBuilder();
        
        this.roots = new LinkedHashMap();
    }
    
    protected abstract C doBuild();
    
    protected abstract Q doCreateQuery(C c);
    
    protected abstract Root doFrom(Class entityClass);

    @Override
    public D reset() {
        final EntityManager em = this.getEntityManager();
        if(!em.isOpen()) {
            throw new IllegalStateException("EntityManager is not open");
        }
        this.clear();
        this.built = false;
        this.criteriaBuilder = em.getCriteriaBuilder();
        return (D)this;
    }
    
    @Override
    protected void clear() {
        super.clear();
//        built = false; // Added to reset()
        currentEntityType = null;
        joinFromType = null;
        joinToType = null;
        currentFrom = null;
        join = null;
        restriction = null;
        nextConnector = null;
        criteriaBuilder = null;
        roots.clear();
        if(orders != null) {
            orders.clear();
        }
    }
    
    public Q format(Q q) {
        return q;
    }
    
    @Override
    public Q createQuery() {
            
        final C criteriaForQuery = this.build();

        final Q tq = this.doCreateQuery(criteriaForQuery);

        return format(tq);
    }

    @Override
    public C build() {
    
        this.throwExceptionIfBuilt();
        
        this.built = true;

        logger.log(Level.FINER, "#build.\n{0}", this);

        return this.doBuild();
    }
    
    @Override
    public D and() {
        this.throwExceptionIfNullCurrentEntityType();
        return this.and(currentEntityType);
    }
    
    @Override
    public D and(Class entityType) {
        this.throwExceptionIfBuilt();
        return this.connect(entityType, AND);
    }

    @Override
    public D or() {
        this.throwExceptionIfNullCurrentEntityType();
        return this.or(currentEntityType);
    }
    
    @Override
    public D or(Class entityType) {
        this.throwExceptionIfBuilt();
        return this.connect(entityType, OR);
    }
    
    public D connect(Class entityType, Criteria.LogicalOperator connector) {
        
        this.throwExceptionIfBuilt();
        
        if(connector == null) {
            throw new NullPointerException();
        }
        
        this.currentEntityType = entityType;
        
        return this.doConnect(entityType, connector);
    }
    
    @Override
    public D where(String col, Collection values) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.where(this.currentEntityType, col, values);
    }

    @Override
    public D where(Class entityType, String col, Collection values) {
        return this.where(entityType, col, values.toArray(new Object[0]));
    }
    
    @Override
    public D where(String col, Object... values) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.where(this.currentEntityType, col, values);
    }
    
    @Override
    public D where(Class entityType, String col, Object... values) {
        
        this.throwExceptionIfBuilt();

        int offset = 0;
        
        for(Object value:values) {
            if(offset++ < values.length-1) {
                this.where(entityType, col, Criteria.EQ, value, Criteria.OR);
            }else{
                this.where(entityType, col, Criteria.EQ, value);
            }
        }
        
        return (D)this;
    }
    
    
    @Override
    public D where(Map parameters) {
        this.throwExceptionIfNullCurrentEntityType();
        return where(currentEntityType, parameters);
    }
    
    @Override
    public D where(Class entityType, Map parameters) {
        this.throwExceptionIfBuilt();
        Set keys = parameters.keySet();
        for(Object key:keys) {
            this.where(entityType, toString(key), parameters.get(key));
        }
        return (D)this;
    }

    @Override
    public D where(
            Criteria.ComparisonOperator comparisonOperator, Criteria.LogicalOperator connector, Map params) {
        this.throwExceptionIfNullCurrentEntityType();
        return where(currentEntityType, comparisonOperator, connector, params);
    }

    @Override
    public D where(Class entityType, 
            Criteria.ComparisonOperator comparisonOperator, Criteria.LogicalOperator connector, Map params) {
        
        this.throwExceptionIfBuilt();

        int offset = 0;
        
        final Set cols = params.keySet();

        for(Object col : cols) {

            Object val = params.get(col);
            
            logger.finer(() -> toString(col) + '=' + val);

            if(offset++ < params.size()-1) {
                this.where(entityType, toString(col), comparisonOperator, val, connector);
            }else{
                this.where(entityType, toString(col), comparisonOperator, val);
            }
        }
        
        return (D)this;
    }
    
    @Override
    public D where(String key, 
            Criteria.ComparisonOperator comparisonOperator, Object val) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.where(currentEntityType, key, comparisonOperator, val);
    }
    
    @Override
    public D where(Class entityType, String key, 
            Criteria.ComparisonOperator comparisonOperator, Object val) {
        this.throwExceptionIfBuilt();
        return this.where(entityType, key, comparisonOperator, val, Criteria.LogicalOperator.AND);
    }

    @Override
    public D where(String [] cols, 
            Criteria.ComparisonOperator comparisonOperator, 
            Object val, Criteria.LogicalOperator connector) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.where(currentEntityType, cols, comparisonOperator, val, connector);
    }
    
    @Override
    public D where(Class entityType, String [] cols, 
            Criteria.ComparisonOperator comparisonOperator, 
            Object val, Criteria.LogicalOperator connector) {
        this.throwExceptionIfBuilt();
        for(String col:cols) {
            this.where(entityType, col, comparisonOperator, val, connector);
        }
        return (D)this;
    }
    
    @Override
    public D where(String key, 
            Criteria.ComparisonOperator comparisonOperator, Object val, Criteria.LogicalOperator connector) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.where(currentEntityType, key, comparisonOperator, val, connector);
    }

    @Override
    public D where(Class entityType, String key, 
            Criteria.ComparisonOperator comparisonOperator, Object val, Criteria.LogicalOperator connector) {
        
        this.throwExceptionIfBuilt();
        
        if(key == null || comparisonOperator == null) {
            throw new NullPointerException();
        }
        
        this.currentEntityType = entityType;
        
        return this.doWhere(entityType, key, comparisonOperator, val, connector);
    }
    
    @Override
    public D join(String joinColumn, Class toType) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.join(currentEntityType, joinColumn, toType);
    }
    
    @Override
    public D join(Class fromType, String joinColumn, Class toType) {
        this.throwExceptionIfBuilt();
        return this.join(fromType, joinColumn, JoinType.INNER, toType);
    }

    @Override
    public D joins(JoinType joinType, Map<String, Class> joins) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.joins(currentEntityType, joinType, joins);
    }
    
    @Override
    public D joins(Class fromType, JoinType joinType, Map<String, Class> joins) {
        
        this.throwExceptionIfBuilt();
        
        Set<String> keys = joins.keySet();
        for(String key:keys) {
            Class joinEntityType = joins.get(key);
            this.join(fromType, key, joinType, joinEntityType);
        }
        return (D)this;
    }

    @Override
    public D join(String joinColumn, JoinType joinType, Class toType) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.join(currentEntityType, joinColumn, joinType, toType);
    }
    
    @Override
    public D join(Class fromType, String joinColumn, JoinType joinType, Class toType) {
        
        this.throwExceptionIfBuilt();
        
        if(fromType == null || joinColumn == null || joinType == null || toType == null) {
            throw new NullPointerException();
        }
        
        this.currentEntityType = fromType;
        
        return this.doJoin(fromType, joinColumn, joinType, toType);
    }

    @Override
    public D descOrder(String... cols) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.descOrder(currentEntityType, cols);
    }
    
    @Override
    public D descOrder(Class entityType, String... cols) {
        this.throwExceptionIfBuilt();
        return this.orders(entityType, "DESC", cols);
    }

    @Override
    public D descOrder(Collection<String> cols) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.descOrder(currentEntityType, cols);
    }
    
    @Override
    public D descOrder(Class entityType, Collection<String> cols) {
        this.throwExceptionIfBuilt();
        return this.orders(entityType, "DESC", cols.toArray(new String[0]));
    }
    
    @Override
    public D ascOrder(String... cols) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.ascOrder(currentEntityType, cols);
    }
    
    @Override
    public D ascOrder(Class entityType, String... cols) {
        this.throwExceptionIfBuilt();
        return this.orders(entityType, "ASC", cols);
    }

    @Override
    public D ascOrder(Collection<String> cols) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.ascOrder(currentEntityType, cols);
    }
    
    @Override
    public D ascOrder(Class entityType, Collection<String> cols) {
        this.throwExceptionIfBuilt();
        return this.orders(entityType, "ASC", cols.toArray(new String[0]));
    }
    
    public D orders(Class entityType, String order, String... cols) {
        
        this.throwExceptionIfBuilt();
        
        Map<String, String> ordersMap = new LinkedHashMap<>(cols.length, 1.0f);
        for(String col:cols) {
            ordersMap.put(col, order);
        }
        
        return (D)this.orderBy(entityType, ordersMap);
    }

    @Override
    public D orderBy(Map<String, String> orders) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.orderBy(currentEntityType, orders);
    }
    
    @Override
    public D orderBy(Class entityType, Map<String, String> orders) {
        this.throwExceptionIfBuilt();
        Set<String> keys = orders.keySet();
        for(String column:keys) {
            this.orderBy(entityType, column, orders.get(column));
        }
        return (D)this;
    }

    @Override
    public D orderBy(String col, String order) {
        this.throwExceptionIfNullCurrentEntityType();
        return this.orderBy(currentEntityType, col, order);
    }
    
    @Override
    public D orderBy(Class entityType, String col, String order) {

        this.throwExceptionIfBuilt();
        
        if(col == null || order == null) {
            throw new NullPointerException();
        }
        
        this.currentEntityType = entityType;

        return this.doOrderBy(entityType, col, order);
    }
    
    protected D doConnect(Class entityType, Criteria.LogicalOperator connector) {
        
        this.nextConnector = connector;
        
        return (D)this;
    }

    protected D doWhere(Class entityType, String key, 
            Criteria.ComparisonOperator comparisonOperator, Object val, Criteria.LogicalOperator connector) {
        
        final Level logLevel = Level.FINER;
        
        if(logger.isLoggable(logLevel)) {
            logger.log(logLevel, "{0} {1} {2} {3}", 
            new Object[]{entityType.getName(), key, comparisonOperator, val});
        }

        From root = this.from(entityType, true);
        
        Predicate predicate = this.buildPredicate(criteriaBuilder, entityType, root, key, comparisonOperator, val);
        if(logger.isLoggable(logLevel)) {
            logger.log(logLevel, "Restriction: {0}\nPredicate: {1}", 
                    new Object[]{restriction, predicate});
        }
        if(predicate != null) {
        
            if(restriction == null) {
                restriction = predicate;
            }else{
                restriction = this.buildPredicate(criteriaBuilder, restriction, nextConnector, predicate);
            }
        }
        logger.log(logLevel, "Combined: {0}", restriction);

        nextConnector = connector;
        
        return (D)this;
    }
    
    protected D doJoin(Class fromType, String joinColumn, JoinType joinType, Class toType) {
        
        From root = this.from(fromType, true);

        this.join = root.join(joinColumn, joinType);
        
        this.joinFromType = fromType;
        this.joinToType = toType;
        
        return (D)this;
    }

    protected D doOrderBy(Class entityType, String col, final String orderString) {

        From root = this.from(entityType, true);
        
        Order order;
        if(orderString.equalsIgnoreCase("DESC")) {
            order = criteriaBuilder.desc(root.get(col));
        }else if(orderString.equalsIgnoreCase("ASC")) {
            order = criteriaBuilder.asc(root.get(col));
        }else{
            throw new UnsupportedOperationException("Unexpected order value: "+orderString+", only values: DESC and ASC are supported");
        }
        
        if(orders == null) {
            orders = new ArrayList<>();
        }
        orders.add(order);
        
        return (D)this;
    }

    @Override
    public D from(Class entityType) {
        this.from(entityType, true);
        return (D)this;
    }
    
    protected From from(Class entityClass, boolean createIfNone) {
        
        final Level logLevel = Level.FINER;
        
        this.throwExceptionIfNull(entityClass, "Class argument 'entityClass' cannot be null");
        
        this.throwExceptionIfBuilt();
        
        From output;
        
        if(joinToType == entityClass) {
            
            output = join;
            
        }else{
            
            output = roots.get(entityClass);

            if(output == null) {

                if(createIfNone) {

                    Root root = this.doFrom(entityClass);
                    
                    roots.put(entityClass, root);

                    output = root;
                    
                    if(logger.isLoggable(logLevel)) {
                        logger.log(logLevel, "For: {0}, created: {1}", new Object[]{entityClass.getName(), root});
                    }
                }else{

                    throw new IllegalStateException("No selections were made for type: "+entityClass.getName());
                }
            }

            currentFrom = output;
        }
        
        this.setCurrentEntityType(entityClass);
        
        if(logger.isLoggable(logLevel)) {
            logger.log(logLevel, "For: {0}, returning: {1}", new Object[]{entityClass.getName(), output});
        }
        
        return output;
    }
    
    
    public Predicate buildPredicate(
            CriteriaBuilder cb, Class entityType, From from, String col, Criteria.ComparisonOperator comparisonOperator, List list) {
         
        return this.buildPredicate(cb, entityType, from, col, comparisonOperator, list.toArray(new Object[0]));
    }
    
    public Predicate buildPredicate(
            CriteriaBuilder cb, Class entityType, From from, String col, Criteria.ComparisonOperator comparisonOperator, Object [] arr) {
            
        List<Predicate> predicates = new LinkedList<>();

        for(Object e:arr) {

            Predicate predicate = this.buildPredicate(cb, entityType, from, col, comparisonOperator, e);

            if(predicate != null) {
                predicates.add(predicate);
            }
        }

        // Has to be OR
        // 
        return this.buildPredicate(cb, Criteria.LogicalOperator.OR, predicates.toArray(new Predicate[0]));
    }
    
    private final Object NO_DB_VALUE = new Serializable(){
        @Override
        public String toString() {
            return "NO_DATABASE_VALUE";
        }
    };
    
    public Predicate buildPredicate(CriteriaBuilder cb, Class entityType, From from, 
            String col, Criteria.ComparisonOperator comparisonOperator, Object val) {
        
        if(logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "#buildPredicate. Column: {0}, comparisonOperator: {1}, value: {2}",
            new Object[]{col, comparisonOperator, val});
        }

        this.throwExceptionIfBuilt();
        
        final DatabaseFormat databaseFormat = this.getDatabaseFormat();
        
        if(databaseFormat != null) {
            
            if(val == null) {
                
                if(!databaseFormat.isDatabaseColumn(entityType, col)) {
                    
                    val = NO_DB_VALUE;
                }
            }else{
                
                Object update = databaseFormat.toDatabaseFormat(entityType, col, val, NO_DB_VALUE);
            
                if(logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "#buildPredicate. Converted: {0}, to: {1}", new Object[]{val, update});          
                }

                val = update;
            }
        }
        
        Predicate predicate;
        
        if(val == NO_DB_VALUE) {
            
            predicate = null;
            
        }else{
            
            switch(comparisonOperator) {
                case EQUALS:
                    predicate = val == null ? cb.isNull(from.get(col)) : cb.equal(from.get(col), val); 
                    break;
                case NOT_EQUALS:
                    predicate = val == null ? cb.isNotNull(from.get(col)) : cb.notEqual(from.get(col), val); 
                    break;
                case LIKE:    
                    predicate = cb.like(from.get(col), val.toString()); 
                    break;
                case NOT_LIKE:    
                    predicate = cb.notLike(from.get(col), val.toString()); 
                    break;
                case GREATER_OR_EQUALS:
                    predicate = cb.greaterThanOrEqualTo(from.get(col), (Comparable)val); 
                    break;
                case GREATER_THAN:
                    predicate = cb.greaterThan(from.get(col), (Comparable)val); 
                    break;
                case LESS_OR_EQUALS:
                    predicate = cb.lessThanOrEqualTo(from.get(col), (Comparable)val); 
                    break;
                case LESS_THAN:
                    predicate = cb.lessThan(from.get(col), (Comparable)val); 
                    break;
                default:    
                    throw new UnsupportedOperationException("Unexpected query connector: '"+comparisonOperator+"' Only '=' and 'LIKE' are currently supported");                        
            }
        }
        
        return predicate;
    }
    
    public Predicate buildPredicate(CriteriaBuilder cb, Predicate p0, Criteria.LogicalOperator connector, Predicate p1) {

        this.throwExceptionIfBuilt();
        
        Predicate predicate;
        
        switch(connector) {
            case AND:
                predicate = cb.and(p0, p1); 
                break; 
            case OR:
                predicate = cb.or(p0, p1); 
                break; 
            default:
                throw new UnsupportedOperationException("Unexpected query connector: '"+connector+"' Only 'OR' and 'AND' are currently supported");
        }        

        return predicate;
    }
    
    public Predicate buildPredicate(CriteriaBuilder cb, Criteria.LogicalOperator connector, Predicate ...predicates) {

        this.throwExceptionIfBuilt();
        
        Predicate predicate;
        
        if(predicates != null && predicates.length != 0) {
        
            switch(connector) {
                case OR:
                    predicate = cb.or(predicates);  
                    break; 
                case AND:
                    predicate = cb.and(predicates);  
                    break; 
                default:
                    throw new UnsupportedOperationException("Unexpected query connector: '"+connector+"' Only 'OR' and 'AND' are currently supported");
            }
        }else{
            
            predicate = null;
        }
        
        return predicate;
    }
    
    public Path[] getPaths(From root, String... cols) {
        
        final Path [] paths;
        
        if(cols.length == 1) {
            final Path path = this.getPath(root, cols[0]);
            paths = new Path[]{path};
        }else{
            int offset = 0;
            paths = new Path[cols.length];
            for(String col:cols) {
                Path path = root.get(col);
                if(path == null) {
                    throw new IllegalStateException("Column in columns list: "+col+", not found int type: "+root.getJavaType().getName());
                }
                paths[offset++] = path;
            }
        }
        
        return paths;
    }
    
    public Path getPath(From root, String column) {
        
        return getPath(root, column, Object.class);
    }
    
    public <T> Path<T> getPath(From root, String column, Class<T> type) {
        
        final Path<T> path = root.<T>get(column);

        if(path == null) {
            
            throw new IllegalStateException("Column in columns list: "+column+", not found int type: "+root.getJavaType().getName());
        }
        
        return path;
    }
/////////////////////// Begin methods overriden for type ///////////////////////

    @Override
    public D detach(Object entity) {
        super.detach(entity);
        return (D)this;
    }

    @Override
    public D remove(Object entity) {
        super.remove(entity);
        return (D)this;
    }

    @Override
    public D refresh(Object entity) {
        super.refresh(entity);
        return (D)this;
    }
    
    @Override
    public <R> R merge(R entity) {
        return super.merge(entity);
    }

    @Override
    public D persist(Object entity) {
        super.persist(entity);
        return (D)this;
    }

//    @Override
//    public void commit() {
//        super.commit();
//    }

    @Override
    public D begin() {
        super.begin();
        return (D)this;
    }
    
/////////////////////// End methods overriden for type /////////////////////////
    
    protected String toString(Object column) {
        return column instanceof Attribute ? ((Attribute)column).getName() : column.toString();
    }
    
    protected void throwExceptionIfBuilt() {
        if(this.isBuilt()) {
            throw new IllegalStateException("Operation not allowed after #build() method is called");
        }
    }
    
    protected void throwExceptionIfNullCurrentEntityType() {
        this.throwExceptionIfNull(currentEntityType, "Current EntityType == null");
    }
    
    protected void throwExceptionIfNull(Object o, String msg) {
        if(o == null) {
            throw new NullPointerException(msg);
        }
    }

    public final void setCurrentEntityType(Class currentEntityType) {
        this.currentEntityType = currentEntityType;
    }

    public final Class getCurrentEntityType() {
        return currentEntityType;
    }

    public final boolean isBuilt() {
        return built;
    }

    @Override
    public final List<Class> getEntityTypeList() {
        return new ArrayList(roots.keySet());
    }
    
    @Override
    public final Set<Class> getEntityTypes() {
        return new LinkedHashSet(roots.keySet());
    }

    @Override
    public final CriteriaBuilder getCriteriaBuilder() {
        return criteriaBuilder;
    }

    public final Map<Class, Root> getRoots() {
        return roots;
    }

    public final From getCurrentFrom() {
        return currentFrom;
    }

    public final Predicate getRestriction() {
        return restriction;
    }
    
    public final List<Order> getOrders() {
        return orders == null ? Collections.EMPTY_LIST : orders;
    }
    
    public final Class getJoinFromType() {
        return joinFromType;
    }

    public final Class getJoinToType() {
        return joinToType;
    }

    public final Join getJoin() {
        return join;
    }

    public final Criteria.LogicalOperator getNextConnector() {
        return nextConnector;
    }
    
    protected void appendJoin(StringBuilder builder, Join j) {
        Attribute a = j.getAttribute();
        String aName = a == null ? null : a.getName();
        Class aJavaType = a == null ? null : a.getJavaType();
        builder.append("Alias: " + j.getAlias() + ", Attribute#name: " + aName + ", Attribute#javaType" + aJavaType + ", JavaType: " + j.getJavaType());
        this.appendModel(builder.append(", "), j.getModel());
        builder.append("On: " + j.getOn());
    }

    protected void appendFrom(StringBuilder builder, From f) {
        builder.append("Alias: " + f.getAlias() + ", JavaType: " + f.getJavaType());
        this.appendModel(builder.append(", "), f.getModel());
    }
    
    protected void appendModel(StringBuilder builder, Bindable b) {
        Class bjt = b == null ? null : b.getBindableJavaType();
        builder.append("Model#bindableJavaType: "+bjt);
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() +  "{\n  built=" + this.isBuilt() + ", where=" + restriction + ", nextConnector=" + nextConnector + "\n  roots=" + roots + '}';// + "\n  joins=" + joins + '}';
    }
}
