/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.integration.service.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultExternalConfigurationAccessTest
{
	private static final String EXTERNAL_CONFIGURATION = "A";
	DefaultExternalConfigurationAccess classUnderTest = new DefaultExternalConfigurationAccess();
	@Mock
	private AbstractOrderEntryModel orderEntryModel;

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(orderEntryModel.getExternalConfiguration()).thenReturn(EXTERNAL_CONFIGURATION);
	}

	@Test
	public void testGetExternalConfiguration()
	{
		assertEquals(EXTERNAL_CONFIGURATION, classUnderTest.getExternalConfiguration(orderEntryModel));
	}

	@Test
	public void testSetExternalConfiguration()
	{
		classUnderTest.setExternalConfiguration(EXTERNAL_CONFIGURATION, orderEntryModel);
		Mockito.verify(orderEntryModel).setExternalConfiguration(EXTERNAL_CONFIGURATION);
	}
}
