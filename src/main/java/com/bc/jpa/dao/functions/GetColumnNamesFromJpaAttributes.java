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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 26, 2018 1:28:05 PM
 * @deprecated To get the column names use {@link com.bc.jpa.dao.functions.GetColumnNames}
 */
@Deprecated
public class GetColumnNamesFromJpaAttributes implements Function<Class, List<String>> {

    private final EntityManagerFactory emf;

    public GetColumnNamesFromJpaAttributes(EntityManagerFactory emf) {
        this.emf = Objects.requireNonNull(emf);
    }

    @Override
    public List<String> apply(Class entityClass) {
        
        final Set<EntityType<?>> entityTypes = emf.getMetamodel().getEntities();

        Set<Attribute<?, ?>> attributes = Collections.EMPTY_SET; 
        for(EntityType entityType : entityTypes) {
            if(entityClass.equals(entityType.getJavaType())) {
                attributes = entityType.getDeclaredSingularAttributes();// entityType.getDeclaredAttributes();
                break;
            }
        }

        final List<String> columnNames = new ArrayList<>(attributes.size());
        if(!attributes.isEmpty()) {
            for(Attribute attribute : attributes) {
                columnNames.add(attribute.getName());
            }
        }
        return Collections.unmodifiableList(columnNames);
    }
}
