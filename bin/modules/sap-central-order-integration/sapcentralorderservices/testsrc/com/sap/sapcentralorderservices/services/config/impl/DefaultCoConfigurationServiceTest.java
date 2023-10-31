/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderservices.services.config.impl;

import static com.sap.sapcentralorderservices.constants.SapcentralorderservicesConstants.CO_ACTIVE;
import static com.sap.sapcentralorderservices.constants.SapcentralorderservicesConstants.CO_SOURCE_SYSTEM_ID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.SAPConfigurationService;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
/**
 *
 */

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCoConfigurationServiceTest
{


	SAPConfigurationService sapConfigurationService = mock(SAPConfigurationService.class);


	DefaultCoConfigurationService defaultCoConfigurationService = new DefaultCoConfigurationService();

	@Test
	public void getCoSourceSystemIdTest()
	{
		defaultCoConfigurationService.setSapConfigurationService(sapConfigurationService);
		when(sapConfigurationService.getProperty(CO_SOURCE_SYSTEM_ID)).thenReturn(CO_SOURCE_SYSTEM_ID);
		final String res = defaultCoConfigurationService.getCoSourceSystemId();
		Assert.assertEquals(CO_SOURCE_SYSTEM_ID, res);
	}

	@Test
	public void isCoActiveTest()
	{
		defaultCoConfigurationService.setSapConfigurationService(sapConfigurationService);
		when(sapConfigurationService.getProperty(CO_ACTIVE)).thenReturn(true);
		final boolean res = defaultCoConfigurationService.isCoActive();
		Assert.assertTrue(res);
	}

}
