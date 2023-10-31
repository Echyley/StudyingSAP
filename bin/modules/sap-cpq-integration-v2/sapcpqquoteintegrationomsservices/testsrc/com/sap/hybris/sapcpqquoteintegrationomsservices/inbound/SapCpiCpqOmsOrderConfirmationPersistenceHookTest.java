/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegrationomsservices.inbound;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hybris.sapcpqquoteintegrationomsservices.inbound.SapCpiCpqOmsOrderConfirmationPersistenceHook;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.saporderexchangeoms.datahub.inbound.impl.SapOmsDataHubInboundOrderHelper;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapCpiCpqOmsOrderConfirmationPersistenceHookTest {
	
	@InjectMocks
	private SapCpiCpqOmsOrderConfirmationPersistenceHook hook = new SapCpiCpqOmsOrderConfirmationPersistenceHook();
	
	@Mock
	private SapOmsDataHubInboundOrderHelper defaultCpqInboundQuoteOrderHelper = mock(SapOmsDataHubInboundOrderHelper.class);
	
	
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		doNothing().when(defaultCpqInboundQuoteOrderHelper).processOrderConfirmationFromHub(null);
		
	}
	
	@Test
	public void sapCpiCpqOmsOrderConfirmationPersistenceHookTestSAPOrderModel()
	{
		assertEquals(hook.execute(new SAPOrderModel()),Optional.empty());
	}
	
	@Test
	public void sapCpiCpqOmsOrderConfirmationPersistenceHookTestOrderModel()
	{
		OrderModel model = new OrderModel(); 
		assertEquals(hook.execute(model),Optional.of(model));
	}

}
