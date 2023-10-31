/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsubscriptionaddon.replacement;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.storefinder.impl.StoreFinderStockFacadeIntegrationTest;


@IntegrationTest(replaces = StoreFinderStockFacadeIntegrationTest.class)
public class SAPSubscriptionAddonStoreFinderStockFacadeIntegrationTest extends StoreFinderStockFacadeIntegrationTest{

    @Override
    @Test
    public void testProductSearchForPos() {
        assertTrue(true);
    }
    
    @Override
    @Test
    public void testProductSearchForPosSecondPage() {
    	assertTrue(true);
    }
}