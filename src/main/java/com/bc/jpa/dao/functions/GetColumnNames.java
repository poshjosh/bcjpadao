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
import java.util.function.Function;
import javax.persistence.EntityManagerFactory;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 26, 2018 1:28:05 PM
 */
public class GetColumnNames implements Function<Class, List<String>> {

    private final MetaDataAccess mda;
    private final Function<Class, String> getTableName;

    public GetColumnNames(EntityManagerFactory emf) {
        this(new MetaDataAccessImpl(emf));
    }

    public GetColumnNames(MetaDataAccess mda) {
        this(mda, new GetTableName(mda));
    }
    
    public GetColumnNames(MetaDataAccess mda, Function<Class, String> getTableName) {
        this.mda = Objects.requireNonNull(mda);
        this.getTableName = Objects.requireNonNull(getTableName);
    }

    @Override
    public List<String> apply(Class entityClass) {
        
        final String tableName = this.getTableName.apply(entityClass);
        
        final List<String> columnNames = this.apply(tableName);
        
        return columnNames;
    }

    public List<String> apply(String tableName) {
        
        final List<String> columnNames = mda.fetchStringMetaData(tableName, MetaDataAccess.COLUMN_NAME);
        
        return columnNames;
    }
}
