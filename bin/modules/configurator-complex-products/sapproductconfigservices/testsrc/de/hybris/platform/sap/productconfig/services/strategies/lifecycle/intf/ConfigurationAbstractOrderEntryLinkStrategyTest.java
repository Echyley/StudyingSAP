/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


/**
 * Unit test for {@link ConfigurationAbstractOrderEntryLinkStrategy}
 */
@UnitTest
public class ConfigurationAbstractOrderEntryLinkStrategyTest
{

	private final ConfigurationAbstractOrderEntryLinkStrategy classUnderTest = new ConfigurationAbstractOrderEntryLinkStrategy()
	{
		@Override
		public void setDraftConfigIdForCartEntry(final String cartEntryKey, final String configId)
		{
		}

		@Override
		public void setConfigIdForCartEntry(final String cartEntryKey, final String configId)
		{
		}

		@Override
		public void removeSessionArtifactsForCartEntry(final String cartEntryId)
		{
		}

		@Override
		public void removeDraftConfigIdForCartEntry(final String cartEntryKey)
		{
		}

		@Override
		public void removeConfigIdForCartEntry(final String cartEntryKey)
		{
		}

		@Override
		public boolean isDocumentRelated(final String configId)
		{
			return false;
		}

		@Override
		public String getDraftConfigIdForCartEntry(final String cartEntryKey)
		{
			return null;
		}

		@Override
		public String getConfigIdForCartEntry(final String cartEntryKey)
		{
			return null;
		}

		@Override
		public String getCartEntryForDraftConfigId(final String configId)
		{
			return null;
		}

		@Override
		public String getCartEntryForConfigId(final String configId)
		{
			return null;
		}
	};

	@Test(expected = IllegalStateException.class)
	public void testGetAbstractOrderEntryForConfigId()
	{
		classUnderTest.getAbstractOrderEntryForConfigId("configId");
	}

}
