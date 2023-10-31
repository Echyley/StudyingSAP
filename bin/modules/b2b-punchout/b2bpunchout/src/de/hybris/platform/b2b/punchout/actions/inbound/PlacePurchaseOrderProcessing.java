/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.services.CXMLElementBrowser;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;

import de.hybris.platform.core.model.order.CartModel;

import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;

import org.cxml.CXML;
import org.cxml.OrderRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;


/**
 * Places an order using the session shopping cart.
 */
public class PlacePurchaseOrderProcessing
{
	private static final Logger LOG = LoggerFactory.getLogger(PlacePurchaseOrderProcessing.class);
	private CheckoutFacade checkoutFacade;
	private CommerceCartService commerceCartService;
	private CartService cartService;
	private PunchOutSessionService punchOutSessionService;

	public OrderData process()
	{
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Placing an order for cart with code: {}", cartData.getCode());
		}
		try
		{
			if(null == cartData.getDeliveryMode()){
				getCheckoutFacade().setDeliveryModeIfAvailable();
			}

			if (getPunchOutSessionService().isPunchOutSessionCartValid().booleanValue())
			{
				restoreSessionCart();
			}

			final OrderData orderData = getCheckoutFacade().placeOrder();
			// After place older reset the cart valid flag
			getPunchOutSessionService().setPunchOutSessionCartIsValid(null);
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Order with code {} was placed.", orderData.getCode());
			}

			return orderData;
		}
		catch (final InvalidCartException e)
		{
			throw new PunchOutException(HttpStatus.CONFLICT, "Unable to checkout", e);
		}
	}

	/**
	 * Finds an {@link OrderRequest} from the input.
	 *
	 * @param input
	 *           the {@link CXML} input
	 * @return the {@link OrderRequest} from the input
	 */
	protected OrderRequest getOrderRequest(final CXML input)
	{
		final OrderRequest result = new CXMLElementBrowser(input).findRequestByType(OrderRequest.class);
		if (result == null)
		{
			throw new PunchOutException(HttpStatus.CONFLICT, "No OrderRequest in the CXML request data");
		}
		return result;
	}

	/**
	 * restore session cart and cart will be re-calculated
	 */
	protected void restoreSessionCart()
	{
		CartModel cartModel = getCartService().getSessionCart();
		try {
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(cartModel);
			parameter.setIgnoreRecalculation(false);
			commerceCartService.restoreCart(parameter);
		} catch (CommerceCartRestorationException e) {
			throw new PunchOutException(HttpStatus.INTERNAL_SERVER_ERROR,
					String.format("restore cart failed. cartId=%s", cartModel.getCode()), e);
		}
	}

	public void setCheckoutFacade(final CheckoutFacade checkoutFacade)
	{
		this.checkoutFacade = checkoutFacade;
	}

	/**
	 * @return the checkoutFacade
	 */
	protected CheckoutFacade getCheckoutFacade()
	{
		return checkoutFacade;
	}


	/**
	 * @param commerceCartService
	 */
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
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
