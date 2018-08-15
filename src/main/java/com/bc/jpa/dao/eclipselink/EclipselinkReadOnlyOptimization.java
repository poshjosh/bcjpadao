package com.bc.jpa.dao.eclipselink;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.OneToMany;
import javax.persistence.TypedQuery;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.config.QueryHints;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 15, 2018 1:23:13 PM
 */
public class EclipselinkReadOnlyOptimization<T> implements BiFunction<Set<Class>, TypedQuery<T>, TypedQuery<T>>{

    @Override
    public TypedQuery<T> apply(Set<Class> entityTypes, TypedQuery<T> tq) {
        
// http://java-persistence-performance.blogspot.com/2010/08/batch-fetching-optimizing-object-graph.html
// http://java-persistence-performance.blogspot.com/2011/06/how-to-improve-jpa-performance-by-1825.html
//                
        tq.setHint("eclipselink.read-only", "true");
        
// http://vard-lokkur.blogspot.com/2011/05/eclipselink-jpa-queries-optimization.html 
        
        boolean added = false;
        for(Class entityType:entityTypes) {
            
            final String entityName = entityType.getSimpleName();

            final String ch = Character.toString(entityName.charAt(0)).toLowerCase();
            
            Field [] fields = entityType.getDeclaredFields();
            
            for(Field field:fields) {
                
                if(accept(entityTypes, field)) {
                    
                    OneToMany oneToMany = field.getAnnotation(OneToMany.class);

                    if(oneToMany != null) {
                        final String HINT = ch + '.' + field.getName();
                        try{
                            tq.setHint(QueryHints.BATCH, HINT);
                            added = true;
                        }catch(IllegalArgumentException ignore) {
                            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
                                    "While setting {0} = {1} on {2}, encountered: {3}", 
                                    new Object[]{QueryHints.BATCH, HINT, entityName, ignore});
                        }
                    }
                }
            }
        }
        
        if(added) {
            tq.setHint(QueryHints.BATCH_TYPE, BatchFetchType.IN);
        }
        
        return tq;
    }

    public boolean accept(Set<Class> classes, Field field) {
        boolean accepted;
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        if(oneToMany == null) {
            accepted = false;
        }else{
            accepted = false;
            final Type type = field.getGenericType();
// Format:  java.util.XXX<Collection-Element-Type> e.g:  java.util.List<com.looseboxes.pu.entities.Productvariant>
            final String sval = type.toString();
            for(Class cls:classes) {
                if(sval.contains( "<" + cls.getName() + ">")) {
                    accepted = true;
                    break;
                }
            }
        }
        return accepted;
    }
}
