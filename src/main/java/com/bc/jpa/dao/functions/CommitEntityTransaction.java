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

import java.util.function.Function;
import javax.persistence.EntityTransaction;

/**
 * @author Chinomso Bassey Ikwuagwu on Oct 20, 2017 10:40:59 AM
 */
public class CommitEntityTransaction implements Function<EntityTransaction, Boolean> {

    @Override
    public Boolean apply(EntityTransaction t) {
//        System.out.println("0--------------- COMMITTING "+t+" @"+this.getClass().getName());
        Boolean committed = Boolean.FALSE;
        try{
            if (t.isActive()) {
                if (t.getRollbackOnly()) {
                    t.rollback();
                } else {
//                    System.out.println("1--------------- COMMITTING --------------- @"+this.getClass().getName());
                    t.commit();
                    committed = Boolean.TRUE;
                }
            }
        }finally{
            if(t.isActive()) {
                t.rollback();
            }
        }
        return committed;
    }
}
