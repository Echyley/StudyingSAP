/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapS4OrderManagementConfigServiceTest {
	
	@Spy
	@InjectMocks
	DefaultSapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	
	@Mock
	ModuleConfigurationAccess moduleConfigurationAccess;
	
	@Mock
	ModelService modelService;
	
	@Test
	public void testIsS4SynchronousOrderEnabled() {
		
		when(moduleConfigurationAccess.isSAPConfigurationActive()).thenReturn(true);
		when(moduleConfigurationAccess.getProperty(anyString())).thenReturn(true);
		
		assertTrue(sapS4OrderManagementConfigService.isS4SynchronousOrderEnabled());

	}
	
	@Test
	public void testIsS4SynchronousOrderEnabledNoSapConfig() {
		
		when(moduleConfigurationAccess.isSAPConfigurationActive()).thenReturn(false);
		
		assertFalse(sapS4OrderManagementConfigService.isS4SynchronousOrderEnabled());
		
	}

	
	@Test
	public void testIsS4SynchronousOrderHistoryEnabled() {
		
		when(moduleConfigurationAccess.isSAPConfigurationActive()).thenReturn(true);
		when(moduleConfigurationAccess.getProperty(anyString())).thenReturn(true);
		
		assertTrue(sapS4OrderManagementConfigService.isS4SynchronousOrderHistoryEnabled());	
		
	}
	
	@Test
	public void testIsS4SynchronousOrderHistoryEnabledNoSapConfig() {
		
		when(moduleConfigurationAccess.isSAPConfigurationActive()).thenReturn(false);
		
		assertFalse(sapS4OrderManagementConfigService.isS4SynchronousOrderHistoryEnabled());
		
	}
	
	@Test
	public void testIsCartPricingEnabledNoSapConfig() {
		
		when(moduleConfigurationAccess.isSAPConfigurationActive()).thenReturn(false);
		
		assertFalse(sapS4OrderManagementConfigService.isCartPricingEnabled());
		
	}
	
	@Test
	public void testIsCartPricingEnabled() {
		
		when(moduleConfigurationAccess.isSAPConfigurationActive()).thenReturn(true);
		when(moduleConfigurationAccess.getProperty(anyString())).thenReturn(true);
		
		assertTrue(sapS4OrderManagementConfigService.isCartPricingEnabled());
		
	}
	
	@Test
	public void testIsCatalogPricingEnabledNoSapConfig() {
		
		when(moduleConfigurationAccess.isSAPConfigurationActive()).thenReturn(false);
		
		assertFalse(sapS4OrderManagementConfigService.isCatalogPricingEnabled());
		
	}
	
	@Test
	public void testCatalogPricingEnabled() {
		
		when(moduleConfigurationAccess.isSAPConfigurationActive()).thenReturn(true);
		when(moduleConfigurationAccess.getProperty(anyString())).thenReturn(true);
		
		assertTrue(sapS4OrderManagementConfigService.isCatalogPricingEnabled());
		
	}
	
	@Test
	public void testIsCreditCheckActiveNoSapConfig() {
		
		when(moduleConfigurationAccess.isSAPConfigurationActive()).thenReturn(false);
		
		assertFalse(sapS4OrderManagementConfigService.isCreditCheckActive());
		
	}
	
	@Test
	public void testCreditCheckActive() {
		
		when(moduleConfigurationAccess.isSAPConfigurationActive()).thenReturn(true);
		when(moduleConfigurationAccess.getProperty(anyString())).thenReturn(true);
		
		assertTrue(sapS4OrderManagementConfigService.isCreditCheckActive());
		
	}
	
	@Test
	public void testIsATPCheckActiveNoSapConfig() {
		
		when(moduleConfigurationAccess.isSAPConfigurationActive()).thenReturn(false);
		
		assertFalse(sapS4OrderManagementConfigService.isATPCheckActive());
		
	}
	
	@Test
	public void testIsATPCheckActive() {
		
		when(moduleConfigurationAccess.isSAPConfigurationActive()).thenReturn(true);
		when(moduleConfigurationAccess.getProperty(anyString())).thenReturn(true);
		
		assertTrue(sapS4OrderManagementConfigService.isATPCheckActive());
		
	}
	
	@Test
	public void testIsRequestedRetrievalDateFeatureEnabledActive() {
		
		BaseStoreModel baseStore = spy(BaseStoreModel.class);
		when(modelService.getAttributeValue(Mockito.any(BaseStoreModel.class), Mockito.anyString())).thenReturn(true);
		
		assertTrue(sapS4OrderManagementConfigService.isRequestedRetrievalDateFeatureEnabled(baseStore));
	}
	
	@Test
	public void testIsRequestedRetrievalDateFeatureEnabledInactive() {
		
		BaseStoreModel baseStore = spy(BaseStoreModel.class);
		when(modelService.getAttributeValue(Mockito.any(BaseStoreModel.class), Mockito.anyString())).thenReturn(false);
		
		assertFalse(sapS4OrderManagementConfigService.isRequestedRetrievalDateFeatureEnabled(baseStore));
		
	}
}
