/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.outbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.enums.PunchOutOrderOperationAllowed;
import de.hybris.platform.b2b.punchout.services.CXMLBuilder;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.cxml.CXML;
import org.cxml.Message;
import org.cxml.Money;
import org.cxml.PunchOutOrderMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPunchOutOrderMessageGeneratorTest
{
	private static final String BUYER_COOKIE = "This is the buyer cookie: 1CX3L4843PPZO";

	@InjectMocks
	private final DefaultPunchOutOrderMessageGenerator action = new DefaultPunchOutOrderMessageGenerator();

	private final PunchOutOrderMessage punchOutOrder = new PunchOutOrderMessage();
	private final PunchOutSession punchOutSession = new PunchOutSession();

	@Mock
	private Converter<CartModel, PunchOutOrderMessage> cartConverter;
	@Mock
	private PunchOutSessionService punchOutSessionService;
	@Mock
	private CartModel cartModel;

	@Before
	public void prepare()
	{
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		when(currencyModel.getIsocode()).thenReturn("ISO");

		when(cartModel.getTotalPrice()).thenReturn(999.99);
		when(cartModel.getCurrency()).thenReturn(currencyModel);

		punchOutSession.setBuyerCookie(BUYER_COOKIE);
		when(cartConverter.convert(cartModel)).thenReturn(punchOutOrder);
		when(punchOutSessionService.getCurrentPunchOutSession()).thenReturn(punchOutSession);
	}

	@Test
	public void shouldCreatePunchOutOrderMessage()
	{
		Message message = action.generate(cartModel);
		//Default value is edit
		assertThat(punchOutOrder.getPunchOutOrderMessageHeader().getOperationAllowed()).isEqualTo(PunchOutOrderOperationAllowed.EDIT.getCode());
		assertThat(punchOutOrder.getBuyerCookie().getContent().get(0)).isEqualTo(BUYER_COOKIE);
		assertThat(message
				.getPunchOutOrderMessageOrProviderDoneMessageOrSubscriptionChangeMessageOrDataAvailableMessageOrSupplierChangeMessageOrOrganizationChangeMessageOrProductActivityMessage())
				.contains(punchOutOrder);

		final Money money = punchOutOrder.getPunchOutOrderMessageHeader().getTotal().getMoney();
		assertThat(money).hasFieldOrPropertyWithValue("value", String.valueOf(cartModel.getTotalPrice()))
						 .hasFieldOrPropertyWithValue("currency", String.valueOf(cartModel.getCurrency().getIsocode()));
	}
}
