/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertNull;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link SimpleExternalConfigurationAccess}
 */
@UnitTest
public class SimpleExternalConfigurationAccessTest
{
	SimpleExternalConfigurationAccess classUnderTest = new SimpleExternalConfigurationAccess();
	@Mock
	private AbstractOrderEntryModel orderEntryModel;
	private final String externalConfiguration = "A";

	@Test
	public void testExternalConfiguration()
	{
		MockitoAnnotations.initMocks(this);
		assertNull(classUnderTest.getExternalConfiguration(orderEntryModel));
		classUnderTest.setExternalConfiguration(externalConfiguration, orderEntryModel);
		Mockito.verifyZeroInteractions(orderEntryModel);
	}
}
