/*
 * Copyright 2018 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.jpa.dao.functions;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.BiFunction;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 18, 2018 5:57:37 PM
 */
public class GetIdAttribute<X, Y> implements BiFunction<Class<X>, Class<Y>, SingularAttribute<? super X, Y>>, Serializable {

    private final Metamodel metamodel;

    public GetIdAttribute(EntityManagerFactory emf) {
        this(emf.getMetamodel());
    }
    
    public GetIdAttribute(Metamodel model) {
        this.metamodel = Objects.requireNonNull(model);
    }
    
    @Override
    public SingularAttribute<? super X, Y> apply(Class<X> entityClass, Class<Y> idClass) {
        
        final EntityType<X> entityType = metamodel.entity(entityClass);

        final SingularAttribute<? super X, Y> idAttribute = entityType.getId(idClass);

        return idAttribute;
    }
}
