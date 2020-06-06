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

import com.bc.db.meta.access.MetaDataAccess;
import com.bc.db.meta.access.MetaDataAccessImpl;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.persistence.EntityManagerFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Aug 9, 2018 10:53:59 PM
 */
public class GetTableName extends GetTableNameFromAnnotation {

    private final MetaDataAccess mda;

    public GetTableName() {
        this.mda = null;
    }
    
    public GetTableName(EntityManagerFactory emf) {
        this(new MetaDataAccessImpl(emf));
    }
    
    public GetTableName(MetaDataAccess mda) {
        this.mda = Objects.requireNonNull(mda);
    }

    /**
     * Get the table name from the Table annotation or throw Exception
     * @param aClass
     * @return
     * @throws IllegalArgumentException 
     */
    @Override
    public String apply(Class aClass) throws IllegalArgumentException{

        String table = null;
        try{
            table = super.apply(aClass);
        }catch(Exception ignored) { }

        if(table == null && mda != null) {

            final List<String> tables = mda.fetchStringMetaData(
                    null, MetaDataAccess.TABLE_NAME);

            final String typeName = aClass.getSimpleName();
            
            final Predicate<String> matches = 
                    (name) -> name.equalsIgnoreCase(typeName);

            // this stream and the next are mutually exclusive
            table = tables.stream()
                    .filter(matches)
                    .findAny().orElse(null);

            if(table == null) {
                
                // this stream and the previous are mutually exclusive
                table = tables.stream()
                        .map((name) -> name.replaceAll("\\p{Punct}", ""))
                        .filter(matches)
                        .findAny().orElse(null);
            }
        }

        if(table == null) {
            throw new IllegalArgumentException();
        }

        return table;
    }
}
