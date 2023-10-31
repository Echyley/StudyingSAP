/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;

import java.io.FileNotFoundException;

import org.cxml.CXML;
import org.cxml.ItemOut;
import org.cxml.OrderRequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PopulateCartPurchaseOrderProcessingTest
{
	@InjectMocks
	private PopulateCartPurchaseOrderProcessing populateCartPurchaseOrderProcessing;

	private CXML requestXML;
	@Mock
	private CartModel cartModel;
	@Mock
	private Converter<ItemOut, AbstractOrderEntryModel> itemOutConverter;
	@Mock
	private Populator<ItemOut, AbstractOrderEntryModel> orderEntryOverridingPopulator;
	@Mock
	private Populator<OrderRequestHeader, CartModel> orderRequestCartPopulator;
	@Mock
	private PunchOutSessionService punchOutSessionService;


	@Before
	public void setUp() throws FileNotFoundException
	{
		requestXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/sampleOrderRequest.xml");
	}

	@Test
	public void testGenerateCxmlResponse()
	{
		final AbstractOrderEntryModel orderEntryModel = mock(AbstractOrderEntryModel.class);
		doReturn(orderEntryModel).when(itemOutConverter).convert(any(ItemOut.class));

		populateCartPurchaseOrderProcessing.process(requestXML, cartModel);

		verify(itemOutConverter, times(3)).convert(any(ItemOut.class));
		verify(orderEntryOverridingPopulator, times(3))
				.populate(any(ItemOut.class), any(AbstractOrderEntryModel.class));
		verify(orderRequestCartPopulator)
				.populate(any(OrderRequestHeader.class), any(CartModel.class));
	}
}
