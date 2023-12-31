/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.inbound.events;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.sap.orderexchange.constants.DataHubInboundConstants;
import de.hybris.platform.sap.orderexchange.datahub.inbound.DataHubInboundDeliveryHelper;
import de.hybris.platform.sap.orderexchange.datahub.inbound.impl.DefaultDataHubInboundDeliveryHelper;
import de.hybris.platform.sap.orderexchange.inbound.events.DataHubDeliveryTranslator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;


@SuppressWarnings("javadoc")
@UnitTest
public class DataHubDeliveryTranslatorTest
{

	@InjectMocks
	private DataHubDeliveryTranslator classUnderTest;
	@Mock
	private Item processedItem;

	@Mock
	private DataHubInboundDeliveryHelper orderHubService;

	@Before
	public void setUp()
	{
		classUnderTest = new DataHubDeliveryTranslator();
		processedItem = org.mockito.Mockito.mock(Item.class);
		orderHubService = org.mockito.Mockito.mock(DefaultDataHubInboundDeliveryHelper.class);
		classUnderTest.setInboundHelper(orderHubService);
	}

	@Test
	public void testPerformDeliveryNegativeNullCheck() throws ImpExException, JaloInvalidParameterException, JaloSecurityException
	{
		Mockito.when(processedItem.getAttribute(DataHubInboundConstants.CODE)).thenReturn("0815");
		Mockito.doNothing().when(orderHubService).processDeliveryAndGoodsIssue("0815", "4711", null);
		classUnderTest.performImport(null, processedItem);
		org.mockito.Mockito.verifyZeroInteractions(orderHubService);
	}

	@Test
	public void testPerformDeliveryPositiveCheck() throws ImpExException, JaloInvalidParameterException, JaloSecurityException
	{
		Mockito.when(processedItem.getAttribute(DataHubInboundConstants.CODE)).thenReturn("0815");
		Mockito.when(orderHubService.determineWarehouseId("4711")).thenReturn("4711");
		Mockito.doNothing().when(orderHubService).processDeliveryAndGoodsIssue("0815", "4711", null);
		classUnderTest.performImport("4711", processedItem);
		org.mockito.Mockito.verify(orderHubService).processDeliveryAndGoodsIssue("0815", "4711", null);
	}


}
