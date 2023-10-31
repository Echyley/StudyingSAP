/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.entitlementservices.interceptor.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.entitlementservices.enums.EntitlementTimeUnit;
import de.hybris.platform.entitlementservices.model.ProductEntitlementModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@IntegrationTest
public class ProductEntitlementInitDefaultsInterceptorTest extends ServicelayerTest
{
	@Resource
	private ModelService modelService;

	@Test
	public void shouldApplyDefaultsForEndDate()
	{
		final ProductEntitlementModel model = new ProductEntitlementModel();
		modelService.initDefaults(model);
		assertEquals(EntitlementTimeUnit.MONTH, model.getTimeUnit());
		assertEquals(1, (int) model.getTimeUnitStart());
		assertNull(model.getTimeUnitDuration());
	}
}
