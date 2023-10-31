/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.outbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;

import org.cxml.CXML;
import org.cxml.Header;
import org.cxml.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutOrderMessageProcessorTest
{
	@InjectMocks
	private DefaultPunchOutOrderMessageProcessor punchOutOrderMessageProcessor;
	@Mock
	private CartModel cartModel;
	@Mock
	private CartService cartService;
	@Mock
	private Header punchOutHeader;
	@Mock
	private Message punchOutOrder;
	@Mock
	private DefaultPunchOutHeaderGenerator punchOutHeaderGenerator;
	@Mock
	private DefaultPunchOutOrderMessageGenerator punchOutOrderMessageGenerator;

	@Before
	public void setUp()
	{
		when(cartService.getSessionCart()).thenReturn(cartModel);
		when(cartService.hasSessionCart()).thenReturn(true);
		when(punchOutHeaderGenerator.generate()).thenReturn(punchOutHeader);
		when(punchOutOrderMessageGenerator.generate(cartModel)).thenReturn(punchOutOrder);
	}

	@Test
	public void testGenerateCxmlResponse()
	{
		final CXML responseXML = punchOutOrderMessageProcessor.generatecXML();

		assertThat(responseXML).isNotNull();

		verify(cartService).hasSessionCart();
		verify(cartService).getSessionCart();
		verify(punchOutHeaderGenerator).generate();
		verify(punchOutOrderMessageGenerator).generate(cartModel);

		final InOrder inOrder = inOrder(cartService, punchOutHeaderGenerator, punchOutOrderMessageGenerator);
		inOrder.verify(cartService).getSessionCart();
		inOrder.verify(punchOutHeaderGenerator).generate();
		inOrder.verify(punchOutOrderMessageGenerator).generate(cartModel);

		responseXML.getHeaderOrMessageOrRequestOrResponse().get(0).equals(punchOutHeader);
		responseXML.getHeaderOrMessageOrRequestOrResponse().get(1).equals(punchOutOrder);
	}
}
