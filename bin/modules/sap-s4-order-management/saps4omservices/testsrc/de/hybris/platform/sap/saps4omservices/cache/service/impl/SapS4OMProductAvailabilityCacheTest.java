/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.cache.service.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.core.saps4omservices.cache.exceptions.SAPS4OMHybrisCacheException;
import de.hybris.platform.sap.core.saps4omservices.cache.service.CacheAccess;
import de.hybris.platform.sap.core.saps4omservices.cache.service.impl.SapS4OMProductAvailabilityCache;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMProductAvailability;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapS4OMProductAvailabilityCacheTest {
	
	@Spy
	@InjectMocks
	SapS4OMProductAvailabilityCache s4omProductAvailabilityCache;
	
	@Mock
	CacheAccess sapS4OMAtpCheckAvailabilityCacheRegion;

	@Test
	public void testReadCachedProductAvailability() {
		
		ProductModel product = spy(ProductModel.class);
		product.setCode("prod-1");
		when(sapS4OMAtpCheckAvailabilityCacheRegion.get("SAP_ATPprod-10001cust-1")).thenReturn(null);
		
		assertNull(s4omProductAvailabilityCache.readCachedProductAvailability(product, "cust-1", "0001"));
	}
	
	@Test
	public void testCacheProductAvailability() {
		
		SapS4OMProductAvailability availability = spy(SapS4OMProductAvailability.class);
		ProductModel product = spy(ProductModel.class);
		product.setCode("prod-1");
		try {
			doNothing().when(sapS4OMAtpCheckAvailabilityCacheRegion).put("SAP_ATPprod-10001cust-1", availability);
			s4omProductAvailabilityCache.cacheProductAvailability(availability,product, "cust-1", "0001");
			verify(sapS4OMAtpCheckAvailabilityCacheRegion).put("SAP_ATPprod-10001cust-1", availability);
		} catch (SAPS4OMHybrisCacheException e) {
			assertNotNull(e);
		}
		
	}
	
	@Test
	public void testInvalidateProductAvailability() {
		
		ProductModel product = spy(ProductModel.class);
		product.setCode("prod-1");
		try {
			doNothing().when(sapS4OMAtpCheckAvailabilityCacheRegion).remove("SAP_ATPprod-10001cust-1");
			s4omProductAvailabilityCache.invalidateProductAvailability(product, "cust-1", "0001");
			verify(sapS4OMAtpCheckAvailabilityCacheRegion).remove("SAP_ATPprod-10001cust-1");
		} catch (SAPS4OMHybrisCacheException e) {
			assertNotNull(e);
		}
		
	}
}
