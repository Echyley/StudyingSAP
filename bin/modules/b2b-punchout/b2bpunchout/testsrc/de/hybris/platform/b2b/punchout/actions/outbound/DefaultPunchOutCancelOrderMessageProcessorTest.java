/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.outbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutUtils;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;

import org.cxml.CXML;
import org.cxml.Message;
import org.cxml.PunchOutOrderMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutCancelOrderMessageProcessorTest
{
	@InjectMocks
	private final DefaultPunchOutCancelOrderMessageProcessor punchOutCancelOrderMessageProcessor = new DefaultPunchOutCancelOrderMessageProcessor();

	@Mock
	private DefaultPunchOutOrderMessageProcessor punchOutOrderMessageProcessor;
	@Mock
	private CartService cartService;
	@Spy
	private CartModel cartModel;
	@Mock
	private CurrencyModel currency;
	@Spy
	private OrderEntryModel orderEntryModel;
	@Spy
	private ProductModel productModel;

	@Test
	public void testGenerateCxmlResponse() throws IOException
	{
		orderEntryModel.setEntryNumber(Integer.valueOf(0));
		orderEntryModel.setQuantity(1L);
		productModel.setCode("product123");
		orderEntryModel.setProduct(productModel);
		cartModel.setEntries(Arrays.asList((AbstractOrderEntryModel) orderEntryModel));
		cartModel.setCurrency(currency);
		final CXML responseXML = PunchOutUtils.unmarshallCXMLFromFile("b2bpunchout/test/punchoutRequisitionResponseWithOutCartInfo.xml");
		final ArgumentCaptor<CartModel> emptyCart = ArgumentCaptor.forClass(CartModel.class);

		when(cartService.getSessionCart()).thenReturn(cartModel);
		when(punchOutOrderMessageProcessor.process(emptyCart.capture())).thenReturn(responseXML);

		final CXML response = punchOutCancelOrderMessageProcessor.generatecXML();

		verify(cartService).getSessionCart();
		verify(cartModel).getCurrency();
		assertThat(response).isNotNull();
		final PunchOutOrderMessage punchOutOrderMessage = (PunchOutOrderMessage) ((Message)response.getHeaderOrMessageOrRequestOrResponse().get(1))
				.getPunchOutOrderMessageOrProviderDoneMessageOrSubscriptionChangeMessageOrDataAvailableMessageOrSupplierChangeMessageOrOrganizationChangeMessageOrProductActivityMessage().get(0);
		assertThat(punchOutOrderMessage.getItemIn().size()).isZero();
		assertThat(punchOutOrderMessage).hasFieldOrPropertyWithValue("punchOutOrderMessageHeader.total.money.value", "0.0");

		final InOrder inOrder = inOrder(cartService, punchOutOrderMessageProcessor);
		inOrder.verify(cartService).getSessionCart();
		inOrder.verify(punchOutOrderMessageProcessor).process(any());
	}
}
