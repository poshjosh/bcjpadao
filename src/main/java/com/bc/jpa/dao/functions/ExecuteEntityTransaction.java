/*
 * Copyright 2017 NUROX Ltd.
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

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 20, 2017 2:49:00 PM
 */
public class ExecuteEntityTransaction<R> implements BiFunction<EntityManager, Function<EntityManager, R>, R> {

    private final Function<EntityTransaction, Boolean> commitTransaction;

    public ExecuteEntityTransaction() {
        this(new CommitEntityTransaction());
    }

    public ExecuteEntityTransaction(Function<EntityTransaction, Boolean> commitTransaction) {
        this.commitTransaction = Objects.requireNonNull(commitTransaction);
    }
    
    @Override
    public R apply(EntityManager em, Function<EntityManager, R> action) {
        R result;
        final EntityTransaction t = em.getTransaction();
        try{
            
            t.begin();
            
            result = action.apply(em);
            
        }finally{
            try{
                this.commitTransaction.apply(t);
            }finally{
                if(em.isOpen()) {
                    em.close();
                }
            }
        }
        return result;
    }
}
