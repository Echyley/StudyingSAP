/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.integrationservices.config.IntegrationServicesConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link ProductConfigurationPassportServiceImpl}
 */
@UnitTest
public class ProductConfigurationPassportServiceImplTest
{

	private static final String ACTION_DESCRIPTION = "Description";
	private static final String SYSTEM_ID = "System";
	private static final String PASSPORT_USER = "Passport user";
	private ProductConfigurationPassportServiceImpl classUnderTest;

	@Mock
	private IntegrationServicesConfiguration integrationServicesConfiguration;


	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigurationPassportServiceImpl(integrationServicesConfiguration);
		Mockito.when(integrationServicesConfiguration.getSapPassportSystemId()).thenReturn(SYSTEM_ID);
		Mockito.when(integrationServicesConfiguration.getSapPassportUser()).thenReturn(PASSPORT_USER);

	}

	@Test
	public void testGenerate()
	{
		final String passportAsString = classUnderTest.generate(ACTION_DESCRIPTION);
		assertNotNull(passportAsString);
	}


	@Test
	public void testgetUuidAsByteArray()
	{
		final byte[] uuid = classUnderTest.getUuidAsBytes();
		assertNotNull(uuid);
		assertEquals(16, uuid.length);
	}

	@Test
	public void testgetUuidAsByteArrayMultipleUUIDs()
	{
		final byte[] uuid = classUnderTest.getUuidAsBytes();
		assertNotNull(uuid);
		assertNotEquals("UUIDs should not be equal", classUnderTest.getUuidAsBytes(), uuid);
	}

	@Test
	public void testgetUuidAsString()
	{
		final String uuid = classUnderTest.getUuidAsString();
		assertNotNull(uuid);
		assertEquals(32, uuid.length());
	}

	@Test
	public void testgetUuidAsStringUpperCase()
	{
		final String uuid = classUnderTest.getUuidAsString();
		assertEquals(uuid.toUpperCase(), uuid);
	}
}
