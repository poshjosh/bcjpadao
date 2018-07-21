/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bc.jpa.dao;

import com.bc.tasktracker.jpa.entities.Task;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Josh
 */
public class DaoImplTest {
    
    public static final EntityManagerFactory EMF = 
            Persistence.createEntityManagerFactory("bctasktrackerPUmaster");
            
    public DaoImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
        if(EMF.isOpen()) {
            EMF.close();
        }
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of count(String) method, of class DaoImpl.
     */
    @Test
    public void testCount() {
        System.out.println("testCount");
        final EntityManager em = EMF.createEntityManager();
        try(final Dao instance = new DaoImpl(em)) {
            final Number count = instance.forSelect(Number.class)
                    .from(Task.class)
                    .count("taskid")
                    .createQuery()
                    .getSingleResult();
            System.out.println("Count: " + count);
        }
    }
}
