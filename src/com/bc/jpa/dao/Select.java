package com.bc.jpa.dao;

import javax.persistence.criteria.CriteriaQuery;

/**
 * @param <T>
 * @author Josh
 */
public interface Select<T> 
        extends CriteriaForSelect<T>,
        SelectDao<T> {

    CriteriaQuery getCriteriaQuery();
}
