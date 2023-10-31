/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.cache;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.pricing.CPSMasterDataVariantPriceKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.pricing.CPSValuePrice;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;


@UnitTest
public class CPSCacheTest
{
	private static final String CONFIG_ID = "c123";
	private final CPSCacheStable classUnderTest = new CPSCacheStable();

	@Test
	public void testRemoveConfigurationDefault()
	{
		classUnderTest.removeConfiguration(CONFIG_ID, Collections.EMPTY_MAP);
		assertEquals(CONFIG_ID, classUnderTest.removedConfigid);
	}

	private static class CPSCacheStable implements CPSCache
	{

		public String removedConfigid;

		@Override
		public void setValuePricesMap(final String kbId, final String pricingProduct,
				final Map<CPSMasterDataVariantPriceKey, CPSValuePrice> valuePricesMap)
		{
			//empty
		}

		@Override
		public Map<CPSMasterDataVariantPriceKey, CPSValuePrice> getValuePricesMap(final String kbId, final String pricingProduct)
		{
			return null;
		}

		@Override
		public void setCookies(final String configId, final List<String> cookieList)
		{
			//empty
		}

		@Override
		public List<String> getCookies(final String configId)
		{
			return null;
		}

		@Override
		public void removeCookies(final String configId)
		{
			//empty
		}

		@Override
		public CPSConfiguration getConfiguration(final String configId)
		{
			return null;
		}

		@Override
		public void removeConfiguration(final String configId)
		{
			removedConfigid = configId;
		}

		@Override
		public void setConfiguration(final String configId, final CPSConfiguration configuration)
		{

		}

	}
}
