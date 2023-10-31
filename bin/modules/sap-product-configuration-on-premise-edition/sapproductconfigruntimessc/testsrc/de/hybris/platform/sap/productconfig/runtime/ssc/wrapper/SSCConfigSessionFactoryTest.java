/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.wrapper;

import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Test;


@UnitTest
public class SSCConfigSessionFactoryTest
{
	private final SSCConfigSessionFactory classUnderTest = new SSCConfigSessionFactory();

	@Test
	public void testConstructor()
	{
		assertNotNull(classUnderTest);
	}
}
