/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;

import java.util.Date;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;


/**
 * Unit test for {@link ProductConfigurationService}
 */
@UnitTest
public class ProductConfigurationServiceTest
{

	private final ProductConfigurationServiceStable defaultService = new ProductConfigurationServiceStable();

	@Test
	public void testReleaseSessionDefault()
	{
		defaultService.releaseSession("123", true);
		assertEquals("123", defaultService.releasedId);
	}

	@Test
	public void testReleaseSessionNotDefault()
	{
		defaultService.releaseSession("123", false);
		assertEquals("123", defaultService.releasedId);
	}

	@Test
	public void testCreateConfigurationFromExternalDefault()
	{
		final KBKeyImpl kbKey = new KBKeyImpl("p123");
		defaultService.createConfigurationFromExternal(kbKey, "extConfig", "123");
		// check that defualt implementation redirects the call
		assertEquals("extConfig", defaultService.externalConfiguration);
		assertSame(kbKey, defaultService.kbKey);
	}

	@Test(expected = NotImplementedException.class)
	public void testCountNumberOfIncompleteCstics()
	{
		defaultService.countNumberOfIncompleteCstics(null);
	}

	@Test(expected = NotImplementedException.class)
	public void testCountNumberOfSolvableConflicts()
	{
		defaultService.countNumberOfSolvableConflicts(null);
	}

	@Test
	public void retrieveConfigurationModelWithGroupId() {
		assertSame(ProductConfigurationServiceStable.modelGet, defaultService.retrieveConfigurationModel(null, null));
	}
	@Test
	public void retrieveConfigurationOverview() {
		assertSame(ProductConfigurationServiceStable.modelGet, defaultService.retrieveConfigurationOverview(null));
	}

	/**
	 * In case you added a method to interface {@link ProductConfigurationService}, you might get a compile error in this
	 * class. Then do NOT add a dummy implementation here, instead please always add a default implementation to your new
	 * interface method.
	 */
	private static class ProductConfigurationServiceStable implements ProductConfigurationService
	{
		private String externalConfiguration;
		private KBKey kbKey;
		private String releasedId;

		private static final ConfigModel modelCreate = new ConfigModelImpl();
		private static final ConfigModel modelGet = new ConfigModelImpl();


		@Override
		public ConfigModel createDefaultConfiguration(final KBKey kbKey)
		{
			return modelCreate;
		}

		@Override
		public ConfigModel createConfigurationForVariant(final String baseProductCode, final String variantProductCode)
		{
			return modelCreate;
		}

		@Override
		public void updateConfiguration(final ConfigModel model)
		{
			// empty
		}

		@Override
		public ConfigModel retrieveConfigurationModel(final String configId)
		{
			return modelGet;
		}

		@Override
		public String retrieveExternalConfiguration(final String configId)
		{
			return null;
		}

		@Override
		public ConfigModel createConfigurationFromExternal(final KBKey kbKey, final String externalConfiguration)
		{
			this.kbKey = kbKey;
			this.externalConfiguration = externalConfiguration;
			return null;
		}

		@Override
		public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
		{
			return null;
		}

		@Override
		public void releaseSession(final String configId)
		{
			this.releasedId = configId;
		}

		@Override
		public int calculateNumberOfIncompleteCsticsAndSolvableConflicts(final String configId)
		{
			return 0;
		}

		@Override
		public boolean hasKbForDate(final String productCode, final Date kbDate)
		{
			return false;
		}

		@Override
		public int getTotalNumberOfIssues(final ConfigModel configModel)
		{
			return 0;
		}

		@Override
		public boolean isKbVersionValid(final KBKey kbKey)
		{
			return false;
		}

		@Override
		public KBKey extractKbKey(final String productCode, final String externalConfig)
		{
			return null;
		}

	}
}
