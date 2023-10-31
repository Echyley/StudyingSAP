/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryLineItemData;


/**
 * Unit test for {@link ConfigurationService}
 */
@UnitTest
public class ConfigurationServiceTest
{
	private static final String CONFIG_ID = "configId";
	private static final AbstractOrderEntryModel ENTRY = new AbstractOrderEntryModel();
	private static final ConfigurationSummaryData CONFIGURATION_SUMMARY_DATA = new ConfigurationSummaryData();

	ConfigurationService classUnderTest = new ConfigurationServiceForTest();

	@Test
	public void testGetConfigurationSummary()
	{
		assertEquals(CONFIGURATION_SUMMARY_DATA, classUnderTest.getConfigurationSummary(CONFIG_ID, ENTRY));
	}

	@Test
	public void testGetNumberOfConfigurationIssues()
	{
		assertEquals(0, classUnderTest.getNumberOfConfigurationIssues(CONFIG_ID, ENTRY));
	}

	@Test
	public void testHasConfigurationIssues()
	{
		assertFalse(classUnderTest.hasConfigurationIssues(CONFIG_ID, ENTRY));
	}

	private static final class ConfigurationServiceForTest implements ConfigurationService
	{
		@Override
		public String createConfiguration(final String productCode)
		{
			// Auto-generated method stub
			return null;
		}

		@Override
		public ConfigurationSummaryData getConfigurationSummary(final String configId)
		{
			return CONFIGURATION_SUMMARY_DATA;
		}

		@Override
		public boolean deleteConfiguration(final String configId)
		{
			// Auto-generated method stub
			return false;
		}

		@Override
		public String cloneConfiguration(final String configId, final boolean permanent)
		{
			// Auto-generated method stub
			return null;
		}

		@Override
		public void removeCachedConfigurationSummary(final String configId)
		{
			// Auto-generated method stub
		}

		@Override
		public boolean hasConfigurationIssues(final String configId)
		{
			// Auto-generated method stub
			return false;
		}

		@Override
		public int getNumberOfConfigurationIssues(final String configId)
		{
			// Auto-generated method stub
			return 0;
		}

		@Override
		public void makeConfigurationPermanent(final String configId)
		{
			// Auto-generated method stub
		}

		@Override
		public List<ConfigurationSummaryLineItemData> getLineItems(final String configId)
		{
			// Auto-generated method stub
			return null;
		}
	}

}
