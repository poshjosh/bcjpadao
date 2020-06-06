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
package com.bc.jpa.dao;

import com.bc.jpa.dao.eclipselink.JpaObjectFactoryEclipselinkOptimized;
import com.bc.jpa.dao.functions.EntityManagerFactoryCreator;
import com.bc.jpa.dao.functions.EntityManagerFactoryCreatorImpl;
import com.bc.jpa.dao.sql.MySQLDateTimePatterns;
import com.bc.jpa.dao.sql.SQLDateTimePatterns;
import com.bc.jpa.dao.util.ContextClassLoaderAccessor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Sep 14, 2019 4:18:00 PM
 */
public class JpaObjectFactoryBuilderImpl implements JpaObjectFactory.Builder {

    private static final Logger LOG = Logger.getLogger(JpaObjectFactoryBuilderImpl.class.getName());
    
    private boolean buildAttempted;
    
    private String sqlVendor;

    private String jpaVendor;
    
    private String persistenceUnitName;
    
    private SQLDateTimePatterns sqlDateTimePatterns;
    
    private EntityManagerFactoryCreator entityManagerFactoryCreator;
    
    private ClassLoader classLoader;

    private Properties properties;    

    @Override
    public boolean isBuildAttempted() {
        return buildAttempted;
    }
    
    @Override
    public JpaObjectFactory build() {
        
        if(this.isBuildAttempted()) {
            throw new IllegalStateException("build() method may only be invoked once.");
        }
        
        this.buildAttempted = true;

        if(this.sqlDateTimePatterns == null) {
            final String v = this.sqlVendor == null ? "" : this.sqlVendor.toLowerCase();
            switch(v) {
                case JpaObjectFactory.Builder.SQL_VENDOR_MYSQL:
                    this.sqlDateTimePatterns = new MySQLDateTimePatterns();
                    break;
                default:
            }
        }
        
        if(this.entityManagerFactoryCreator == null) {
            
            if(this.classLoader == null && this.properties == null) {
                this.entityManagerFactoryCreator = new EntityManagerFactoryCreatorImpl();
            }else if(this.classLoader == null && this.properties != null) {
                this.entityManagerFactoryCreator = new EntityManagerFactoryCreatorImpl(
                        this.createPropertiesProvider()
                );
            }else if(this.classLoader != null && this.properties == null) {
                this.entityManagerFactoryCreator = new EntityManagerFactoryCreatorImpl(
                        this.classLoader
                );
            }else{
                this.entityManagerFactoryCreator = new EntityManagerFactoryCreatorImpl(
                        this.classLoader, this.createPropertiesProvider()
                );
            }
        }
        
        Objects.requireNonNull(this.persistenceUnitName);
        Objects.requireNonNull(this.entityManagerFactoryCreator);
        Objects.requireNonNull(this.sqlDateTimePatterns);
        
        final JpaObjectFactory output;
        final String v = (jpaVendor == null) ? "" : jpaVendor.toLowerCase();
        switch(v) {
            case JpaObjectFactory.Builder.JPA_VENDOR_ECLIPELINK:
                output = new JpaObjectFactoryEclipselinkOptimized(persistenceUnitName,
                    entityManagerFactoryCreator, sqlDateTimePatterns);
                break;
            default:
                output = new JpaObjectFactoryImpl(persistenceUnitName,
                    entityManagerFactoryCreator, sqlDateTimePatterns);
        }

        return output;
    }
    
    private Function<String, Properties> createPropertiesProvider() {
        final Function<String, Properties> propsProvider = this.properties == null ?
                (puName) -> new Properties() : (puName) -> this.properties;
        return propsProvider;
    }

    public String getSqlVendor() {
        return sqlVendor;
    }

    @Override
    public JpaObjectFactory.Builder sqlVendor(String vendor) {
        this.sqlVendor = vendor;
        return this;
    }

    public String getJpaVendor() {
        return jpaVendor;
    }

    @Override
    public JpaObjectFactory.Builder jpaVendor(String vendor) {
        this.jpaVendor = vendor;
        return this;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public JpaObjectFactoryBuilderImpl classLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public JpaObjectFactoryBuilderImpl property(String name, String value) {
        if(this.properties == null) {
            this.properties = new Properties();
        }
        this.properties.setProperty(name, value);
        return this;
    }

    @Override
    public JpaObjectFactoryBuilderImpl properties(String location) {
        final File file = new File(location);
        InputStream in = null;
        try{
            in = new FileInputStream(file);
        }catch(FileNotFoundException fnfe) {
            final ClassLoader cl = this.classLoader == null ? new ContextClassLoaderAccessor().get() : this.classLoader;
            in = cl.getResourceAsStream(location);
            if(in == null) {
                final IllegalArgumentException e = new IllegalArgumentException(location);
                e.addSuppressed(fnfe);
                throw e;
            }
        }
        try{
            if(this.properties == null) {
                this.properties = new Properties();
            }
            this.properties.load(in);
            LOG.log(Level.INFO, "Loaded JPA properties: {0}", properties.stringPropertyNames());            
        }catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return this;
    }

    @Override
    public JpaObjectFactoryBuilderImpl properties(Properties properties) {
        this.properties = properties;
        return this;
    }
    
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }

    @Override
    public JpaObjectFactoryBuilderImpl persistenceUnitName(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
        return this;
    }

    public SQLDateTimePatterns getSqlDateTimePatterns() {
        return sqlDateTimePatterns;
    }

    @Override
    public JpaObjectFactoryBuilderImpl sqlDateTimePatterns(SQLDateTimePatterns sqlDateTimePatterns) {
        this.sqlDateTimePatterns = sqlDateTimePatterns;
        return this;
    }

    public EntityManagerFactoryCreator getEntityManagerFactoryCreator() {
        return entityManagerFactoryCreator;
    }

    @Override
    public JpaObjectFactoryBuilderImpl entityManagerFactoryCreator(EntityManagerFactoryCreator entityManagerFactoryCreator) {
        this.entityManagerFactoryCreator = entityManagerFactoryCreator;
        return this;
    }
}
