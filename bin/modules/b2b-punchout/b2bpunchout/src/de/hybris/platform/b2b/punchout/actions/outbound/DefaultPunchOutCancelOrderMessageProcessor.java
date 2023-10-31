/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.outbound;

import de.hybris.platform.b2b.punchout.services.PunchOutService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;

import org.cxml.CXML;

import java.util.Collections;

/**
 * Default implementation of {@link PunchOutService}.
 */
public class DefaultPunchOutCancelOrderMessageProcessor implements PunchOutOutboundProcessor
{
	private CartService cartService;
	private DefaultPunchOutOrderMessageProcessor punchOutOrderMessageProcessor;

	@Override
	public CXML generatecXML()
	{
		//cancellation means we want to return an empty cart
		final CartModel emptyCartModel = new CartModel();
		emptyCartModel.setCurrency(getCartService().getSessionCart().getCurrency());
		emptyCartModel.setEntries(Collections.emptyList());
		emptyCartModel.setTotalPrice(0D);
		return getPunchOutOrderMessageProcessor().process(emptyCartModel);
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the punchOutOrderMessageProcessor
	 */
	protected DefaultPunchOutOrderMessageProcessor getPunchOutOrderMessageProcessor()
	{
		return punchOutOrderMessageProcessor;
	}

	/**
	 * @param defaultPunchOutOrderMessageProcessor the punchOutOrderMessageProcessor to set
	 */
	public void setPunchOutOrderMessageProcessor(final DefaultPunchOutOrderMessageProcessor defaultPunchOutOrderMessageProcessor)
	{
		this.punchOutOrderMessageProcessor = defaultPunchOutOrderMessageProcessor;
	}

}
