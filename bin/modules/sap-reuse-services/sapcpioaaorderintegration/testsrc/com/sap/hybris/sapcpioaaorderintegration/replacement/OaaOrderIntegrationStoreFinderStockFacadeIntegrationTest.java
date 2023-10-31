/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpioaaorderintegration.replacement;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.storefinder.impl.StoreFinderStockFacadeIntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import org.junit.Test;

import java.io.IOException;

import static de.hybris.platform.warehousing.constants.WarehousingTestConstants.ENCODING;

/**
 * Re-implements test {@link StoreFinderStockFacadeIntegrationTest} to provide missing information required when
 * warehousing extensions is present
 */
@IntegrationTest(replaces = StoreFinderStockFacadeIntegrationTest.class)
public class OaaOrderIntegrationStoreFinderStockFacadeIntegrationTest extends StoreFinderStockFacadeIntegrationTest
{
    @Override
    public void prepare() throws Exception
    {
        super.prepare();
        insertExtraInformation();
    }

    /**
     * Import impex during setup to add relation between warehouse and delivery mode as well as the default ATP formula
     * for the used basestore.
     *
     * @throws IOException
     * @throws ImpExException
     */
    private void insertExtraInformation() throws IOException, ImpExException
    {
        importCsv("/sapcpioaaorderintegration/test/impex/replacement/replacement-store-finder-stock-test-data.impex",
                ENCODING);
        importCsv("/sapcpioaaorderintegration/test/impex/replacement/replacement-add-formula-teststore.impex",
                ENCODING);
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