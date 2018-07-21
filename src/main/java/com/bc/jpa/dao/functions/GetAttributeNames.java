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
import javax.persistence.metamodel.Attribute;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 24, 2017 9:05:59 AM
 */
public class GetAttributeNames implements Function<Attribute[], String[]> {

    @Override
    public String[] apply(Attribute[] attributes) {
        final String [] names = attributes == null ? null : new String [attributes.length];
        if(attributes != null) {
            for(int i=0; i<attributes.length; i++) {
                names[i] = attributes[i].getName();
            }
        }
        return names;
    }
}
