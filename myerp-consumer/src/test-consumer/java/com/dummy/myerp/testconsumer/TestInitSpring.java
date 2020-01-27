package com.dummy.myerp.testconsumer;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class TestInitSpring extends ConsumerTestCase {

	/**
     * Constructeur.
     */
    public TestInitSpring() {
        super();
    }


    /**
     * Teste l'initialisation du contexte Spring
     */
    @Test
    public void testInit() {
        SpringRegistry.init();
        assertNotNull(SpringRegistry.getDaoProxy());
    }
}
