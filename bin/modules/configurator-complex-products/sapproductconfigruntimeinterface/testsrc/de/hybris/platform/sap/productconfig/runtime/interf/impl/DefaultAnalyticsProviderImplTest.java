/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import static org.junit.Assert.assertFalse;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;

import org.junit.Before;
import org.junit.Test;


@UnitTest
public class DefaultAnalyticsProviderImplTest
{
	private DefaultAnalyticsProviderImpl classUnderTest;

	@Before
	public void setup()
	{
		classUnderTest = new DefaultAnalyticsProviderImpl();
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetPopularity()
	{
		classUnderTest.getPopularity(new ConfigModelImpl());
	}

	@Test
	public void testIsActive()
	{
		assertFalse(classUnderTest.isActive());
	}
}
