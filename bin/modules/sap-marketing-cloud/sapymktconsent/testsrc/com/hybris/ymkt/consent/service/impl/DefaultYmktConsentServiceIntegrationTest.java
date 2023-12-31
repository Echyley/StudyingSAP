/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.consent.service.impl;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.hybris.ymkt.common.consent.YmktConsentService;


@IntegrationTest
public class DefaultYmktConsentServiceIntegrationTest extends ServicelayerBaseTest
{

	@Resource(name = "ymktConsentService")
	YmktConsentService ymktConsentService;


	@Test
	public void testGetAnonymousUserConsent()
	{
		Assert.assertFalse(ymktConsentService.getUserConsent("---"));
	}

	@Test
	public void testGetRegisteredUserConsent()
	{
		Assert.assertFalse(ymktConsentService.getUserConsent("", "---"));
	}

}
