/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services;

import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;

import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;


/**
 * Unit test for {@link ConfigurationServiceLayerHelper}
 */
@UnitTest
public class ConfigurationServiceLayerHelperTest
{
	ConfigurationServiceLayerHelper classUnderTest = new ConfigurationServiceLayerHelperForTest();

	@Test
	public void testRetrieveUserForAbstractOrderEntryIfRelevant()
	{
		assertNull(classUnderTest.retrieveUserForAbstractOrderEntryIfRelevant(new AbstractOrderEntryModel()));
	}

	private static final class ConfigurationServiceLayerHelperForTest implements ConfigurationServiceLayerHelper
	{
		@Override
		public CloudCPQOrderEntryProductInfoModel getCPQInfo(final AbstractOrderEntryModel entry)
		{
			// Auto-generated method stub
			return null;
		}

		@Override
		public void ensureBaseSiteSetAndExecuteConfigurationAction(final AbstractOrderModel order,
				final Consumer<BaseSiteModel> action)
		{
			// Auto-generated method stub
		}

		@Override
		public <T> void processPageWise(final IntFunction<SearchPageData<T>> searchFunction,
				final Consumer<List<T>> searchResultConsumer)
		{
			// Auto-generated method stub
		}
	}

}
