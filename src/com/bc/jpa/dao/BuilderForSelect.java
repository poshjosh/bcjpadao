package com.bc.jpa.dao;

import javax.persistence.criteria.CriteriaQuery;

/**
 * @param <T>
 * @author Josh
 */
public interface BuilderForSelect<T> 
        extends CriteriaForSelect<T>,
        SelectDao<T> {

    CriteriaQuery getCriteriaQuery();
}
