/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;


@UnitTest
public class DefaultPricingProviderImplTest
{
	private DefaultPricingProviderImpl classUnderTest;

	@Before
	public void setup()
	{
		classUnderTest = new DefaultPricingProviderImpl();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetPriceSummary() throws PricingEngineException
	{
		final String configId = "1";
		final ConfigurationRetrievalOptions options = new ConfigurationRetrievalOptions();
		classUnderTest.getPriceSummary(configId, options);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testFillValuePrices()
	{
		classUnderTest.fillValuePrices(new ConfigModelImpl());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testFillValuePricesForValues()
	{
		final List<PriceValueUpdateModel> updateModels= new ArrayList<PriceValueUpdateModel>();
		final String kbId ="123";
		classUnderTest.fillValuePrices(updateModels, kbId);
	}

	@Test
	public void testIsActive()
	{
		assertFalse(classUnderTest.isActive());
	}
}
