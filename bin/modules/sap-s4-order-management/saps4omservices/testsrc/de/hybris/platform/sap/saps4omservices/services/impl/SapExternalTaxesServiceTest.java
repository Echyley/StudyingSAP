/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.externaltax.DecideExternalTaxesStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapExternalTaxesServiceTest {

	@InjectMocks
	SapExternalTaxesService sapExternalTaxesService;
	
	@Mock
	SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	
	@Spy
	DecideExternalTaxesStrategy decideExternalTaxesStrategy;
	
	@Mock
	AbstractOrderModel abstractOrder;
	
	@Before
	public void init() {
		sapExternalTaxesService.setSapS4OrderManagementConfigService(sapS4OrderManagementConfigService);
	}
	
	@Test
	public void testCalculateExternalTaxes() {

		when(sapS4OrderManagementConfigService.isCatalogPricingEnabled()).thenReturn(true);
		boolean result = sapExternalTaxesService.calculateExternalTaxes(abstractOrder);
		assertTrue(result);
	}
	
	@Test
	public void testCalculateExternalTaxesWithSuperCall() {
		when(decideExternalTaxesStrategy.shouldCalculateExternalTaxes(abstractOrder)).thenReturn(false);
		when(sapS4OrderManagementConfigService.isCatalogPricingEnabled()).thenReturn(false);
		boolean result = sapExternalTaxesService.calculateExternalTaxes(abstractOrder);
		assertFalse(result);
	}
		
}
