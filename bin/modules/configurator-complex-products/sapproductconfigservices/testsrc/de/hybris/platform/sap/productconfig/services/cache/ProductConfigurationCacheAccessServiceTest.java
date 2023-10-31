/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ProductConfigurationCacheAccessServiceTest
{

	private static final String CONFIG_ID = null;
	private ProductConfigurationCacheAccessServiceStable classUnderTest;
	private final Map<String, String> map = new HashMap<>();

	@Before
	public void setUp()
	{
		classUnderTest = new ProductConfigurationCacheAccessServiceStable();
	}

	@Test
	public void testPopulateCacheKeyContextAttributeDefault()
	{
		classUnderTest.populateCacheKeyContextAttributes(map);
		assertTrue(map.isEmpty()); //default does nothing
	}

	@Test
	public void testRemoveConfigAttributeStateDefault()
	{
		classUnderTest.removeConfigAttributeState(CONFIG_ID, map);
		assertEquals(CONFIG_ID, classUnderTest.calledWithConfigId);

	}

	private static class ProductConfigurationCacheAccessServiceStable implements ProductConfigurationCacheAccessService
	{
		public String calledWithConfigId;

		@Override
		public void setAnalyticData(final String configId, final AnalyticsDocument analyticsDocument)
		{
			//empty
		}

		@Override
		public AnalyticsDocument getAnalyticData(final String configId)
		{
			return null;
		}

		@Override
		public PriceSummaryModel getPriceSummaryState(final String configId)
		{
			return null;
		}

		@Override
		public void setPriceSummaryState(final String configId, final PriceSummaryModel priceSummaryModel)
		{
			//empty
		}

		@Override
		public ConfigModel getConfigurationModelEngineState(final String configId)
		{
			return null;
		}

		@Override
		public void setConfigurationModelEngineState(final String configId, final ConfigModel configModel)
		{
			//empty
		}

		@Override
		public void removeConfigAttributeState(final String configId)
		{
			calledWithConfigId = configId;
		}

		@Override
		public Map<String, ClassificationSystemCPQAttributesContainer> getCachedNameMap(final String productCode)
		{
			return null;
		}

	}
}
