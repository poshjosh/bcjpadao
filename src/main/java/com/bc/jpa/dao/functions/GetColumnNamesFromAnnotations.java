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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import javax.persistence.Column;
import javax.persistence.JoinColumn;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 18, 2018 3:30:33 PM
 */
public class GetColumnNamesFromAnnotations implements Function<Class, List<String>>, Serializable {

    @Override
    public List<String> apply(Class entityType) {
        final Field [] fields = entityType.getDeclaredFields();
        final List<String> columnNames = new ArrayList<>(fields.length);
        for (Field field : fields) {
            final Column column = field.getAnnotation(Column.class);
//            System.out.println("@" + this.getClass().getName() + ' ' + 
//                    entityType.getSimpleName() + "#" + field.getName() + " has column: " + column);
            if (column != null) {
                columnNames.add(column.name());
            }else{
                final JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
//                System.out.println("@" + this.getClass().getName() + ' ' + 
//                        entityType.getSimpleName() + "#" + field.getName() + " has join column: " + joinColumn);
                if(joinColumn != null) {
                    columnNames.add(joinColumn.name());
                }
            }
        }
        return Collections.unmodifiableList(columnNames);
    }
}
