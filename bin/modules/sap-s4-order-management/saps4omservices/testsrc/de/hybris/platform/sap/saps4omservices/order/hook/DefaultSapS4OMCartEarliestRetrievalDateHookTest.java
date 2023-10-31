/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.order.hook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.sap.saps4omservices.utils.SapS4OrderUtil;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSapS4OMCartEarliestRetrievalDateHookTest {
	
	@Spy
	@InjectMocks
	DefaultSapS4OMCartEarliestRetrievalDateHook sapS4OMCartEarliestRetrievalDateHook;
	
	@Mock
	SessionService sessionService;
	
	@Mock
	SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	
	@Mock
	SapS4OrderUtil sapS4OrderUtil;
	
	@Test
	public void testGetEarliestRetrievalDates() {
		
		CartModel cart = spy(CartModel.class);
		AbstractOrderEntryModel entry = spy(AbstractOrderEntryModel.class);
		Session session = spy(Session.class);
		
		lenient().when(cart.getCode()).thenReturn("");
		when(cart.getEntries()).thenReturn(Collections.singletonList(entry));
		when(sapS4OrderManagementConfigService.isCartPricingEnabled()).thenReturn(true);
		when(sapS4OrderUtil.isSalesEntry(entry)).thenReturn(true);
		when(sessionService.getCurrentSession()).thenReturn(session);
		when(session.getAttribute(anyString())).thenReturn("9999-12-31");
		
		List<String> result = sapS4OMCartEarliestRetrievalDateHook.getEarliestRetrievalDates(cart);
		
		assertNotNull(result);
		assertEquals(1, result.size());
		
	}
	
	@Test
	public void testGetEarliestRetrievalDatesWhenNotEnabled() {
		
		CartModel cart = spy(CartModel.class);
		AbstractOrderEntryModel entry = spy(AbstractOrderEntryModel.class);
		
		lenient().when(cart.getCode()).thenReturn("");
		when(cart.getEntries()).thenReturn(Collections.singletonList(entry));
		
		List<String> result = sapS4OMCartEarliestRetrievalDateHook.getEarliestRetrievalDates(cart);
		
		assertNotNull(result);
		assertEquals(0, result.size());
		
	}

}
