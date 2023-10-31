/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.test.property;

import org.junit.Assert;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;


/**
 * Property Access Factory test.
 */
@UnitTest
public class PropertyAccessFactoryTest
{
	/**
	 * Tests creation of property access.
	 */
	@Test
	public void testCreatePropertyAccess()
	{
		final PropertyAccessFactory classUnderTest = new PropertyAccessFactory();
		Assert.assertNotNull(classUnderTest.createPropertyAccess());
	}

}
