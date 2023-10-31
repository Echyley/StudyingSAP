/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegrationomsservices.replacement;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.storefinder.impl.StoreFinderStockFacadeIntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import org.junit.Test;

/**
 * Re-implements test {@link StoreFinderStockFacadeIntegrationTest} to provide missing information required when
 * warehousing extensions is present
 */
@IntegrationTest(replaces = StoreFinderStockFacadeIntegrationTest.class)
public class CpqQuoteIntegrationOmsStoreFinderStockFacadeIntegrationTest extends StoreFinderStockFacadeIntegrationTest {

    @Override
    public void prepare() throws Exception {
        super.prepare();
        insertExtraInformation();
    }

    /**
     * Import impex during setup to add relation between warehouse and delivery mode as well as the default ATP formula
     * for the used basestore.
     *
     * @throws ImpExException
     */
    private void insertExtraInformation() throws ImpExException {
        importCsv("/sapcpqquoteintegrationomsservices/test/impex/replacement/replacement-store-finder-stock-test-data.impex", "utf-8");
        importCsv("/sapcpqquoteintegrationomsservices/test/impex/replacement/replacement-add-formula-teststore.impex", "utf-8");
    }

    /**
     * To bypass sonar check 'Add some tests to this class.'
     */
    @Test
    @Override
    public void testProductSearchForPos() {
        super.testProductSearchForPos();
    }

}