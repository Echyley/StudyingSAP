/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.outbound;

import de.hybris.platform.b2b.punchout.services.CXMLBuilder;
import de.hybris.platform.b2b.punchout.services.PunchOutService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import org.cxml.CXML;
import org.cxml.Header;
import org.cxml.Message;

/**
 * Default implementation of {@link PunchOutService}.
 */
public class DefaultPunchOutOrderMessageProcessor implements PunchOutOutboundProcessor
{

	private CartService cartService;
	private DefaultPunchOutHeaderGenerator punchOutHeaderGenerator;
	private DefaultPunchOutOrderMessageGenerator punchOutOrderMessageGenerator;

	@Override
	public CXML generatecXML()
	{
		final CartModel cartModel = getCartModel();

		if (cartModel == null) {
			throw new IllegalStateException("There was no cart in the session found.");
		}
		return process(cartModel);
	}

	protected CXML process(CartModel cartModel)
	{
		final CXML responseCXML = CXMLBuilder.newInstance().create();
		final Header header = getPunchOutHeaderGenerator().generate();
		final Message message = getPunchOutOrderMessageGenerator().generate(cartModel);

		responseCXML.getHeaderOrMessageOrRequestOrResponse().add(header);
		responseCXML.getHeaderOrMessageOrRequestOrResponse().add(message);

		return responseCXML;
	}

	protected CartModel getCartModel()
	{
		if (getCartService().hasSessionCart())
		{
			return getCartService().getSessionCart();
		}
		else
		{
			throw new IllegalStateException("There was no cart in the session found.");
		}
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
	 * @return the punchOutHeaderGenerator
	 */
	protected DefaultPunchOutHeaderGenerator getPunchOutHeaderGenerator()
	{
		return punchOutHeaderGenerator;
	}

	/**
	 * @param defaultPunchOutHeaderGenerator
	 *           the defaultPunchOutHeaderGenerator to set
	 */
	public void setPunchOutHeaderGenerator(
			final DefaultPunchOutHeaderGenerator defaultPunchOutHeaderGenerator)
	{
		this.punchOutHeaderGenerator = defaultPunchOutHeaderGenerator;
	}

	/**
	 * @return the punchOutOrderMessageGenerator
	 */
	protected DefaultPunchOutOrderMessageGenerator getPunchOutOrderMessageGenerator()
	{
		return punchOutOrderMessageGenerator;
	}

	/**
	 * @param defaultPunchOutOrderMessageGenerator
	 *           the punchOutOrderMessageProcessingAction to set
	 */
	public void setPunchOutOrderMessageGenerator(final DefaultPunchOutOrderMessageGenerator defaultPunchOutOrderMessageGenerator)
	{
		this.punchOutOrderMessageGenerator = defaultPunchOutOrderMessageGenerator;
	}
}
