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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 25, 2018 9:19:33 PM
 */
public class GetEntityClasses implements Function<EntityManagerFactory, Set<Class>>, Serializable {

    @Override
    public Set<Class> apply(EntityManagerFactory emf) {
        final Set<EntityType<?>> entityTypes = emf.getMetamodel().getEntities();
        final Set<Class> output = new LinkedHashSet(entityTypes.size());
        for(EntityType entityType : entityTypes) {
            output.add(entityType.getJavaType());
        }
        return Collections.unmodifiableSet(output);
    }
}
