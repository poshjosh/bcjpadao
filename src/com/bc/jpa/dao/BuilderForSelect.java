package com.bc.jpa.dao;

/**
 * @param <T>
 * @author Josh
 */
public interface BuilderForSelect<T> 
        extends CriteriaForSelect<T>,
        SelectDao<T> {

}
