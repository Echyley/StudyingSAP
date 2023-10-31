/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.outbound;

import de.hybris.platform.b2b.punchout.PunchOutSession;
import de.hybris.platform.b2b.punchout.enums.PunchOutOrderOperationAllowed;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.cxml.BuyerCookie;
import org.cxml.Message;
import org.cxml.Money;
import org.cxml.PunchOutOrderMessage;
import org.cxml.PunchOutOrderMessageHeader;
import org.cxml.Total;


/**
 * Create PunchOutOrderMessage.
 */
public class DefaultPunchOutOrderMessageGenerator
{
	private Converter<CartModel, PunchOutOrderMessage> punchOutOrderMessageConverter;
	private PunchOutSessionService punchOutSessionService;

	public Message generate(final CartModel source)
	{
		final PunchOutOrderMessage punchOutOrderMessage = getPunchOutOrderMessageConverter().convert(source);
		final PunchOutSession currentPunchOutSession = getPunchOutSessionService().getCurrentPunchOutSession();
		final BuyerCookie buyerCookie = new BuyerCookie();
		buyerCookie.getContent().add(currentPunchOutSession.getBuyerCookie());
		punchOutOrderMessage.setBuyerCookie(buyerCookie);
		punchOutOrderMessage.setPunchOutOrderMessageHeader(new PunchOutOrderMessageHeader());
		punchOutOrderMessage.getPunchOutOrderMessageHeader().setOperationAllowed(PunchOutOrderOperationAllowed.EDIT.getCode());

		final Total total = new Total();
		total.setMoney(new Money());
		total.getMoney().setCurrency(source.getCurrency().getIsocode());
		total.getMoney().setvalue(String.valueOf(source.getTotalPrice()));

		punchOutOrderMessage.getPunchOutOrderMessageHeader().setTotal(total);

		final Message message = new Message();
		message
				.getPunchOutOrderMessageOrProviderDoneMessageOrSubscriptionChangeMessageOrDataAvailableMessageOrSupplierChangeMessageOrOrganizationChangeMessageOrProductActivityMessage()
				.add(punchOutOrderMessage);
		return message;
	}

	protected Converter<CartModel, PunchOutOrderMessage> getPunchOutOrderMessageConverter()
	{
		return punchOutOrderMessageConverter;
	}

	public void setPunchOutOrderMessageConverter(final Converter<CartModel, PunchOutOrderMessage> punchOutOrderMessageConverter)
	{
		this.punchOutOrderMessageConverter = punchOutOrderMessageConverter;
	}

	protected PunchOutSessionService getPunchOutSessionService()
	{
		return punchOutSessionService;
	}

	public void setPunchOutSessionService(final PunchOutSessionService punchOutSessionService)
	{
		this.punchOutSessionService = punchOutSessionService;
	}

}
