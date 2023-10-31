/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import de.hybris.platform.b2b.punchout.PunchOutException;
import de.hybris.platform.b2b.punchout.services.CXMLElementBrowser;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;

import org.cxml.CXML;
import org.cxml.ItemOut;
import org.cxml.OrderRequest;
import org.cxml.OrderRequestHeader;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;

/**
 * This implementation is meant to process the OrderRequest info from the cXML.
 */
public class PopulateCartPurchaseOrderProcessing
{
	private Converter<ItemOut, AbstractOrderEntryModel> itemOutConverter;
	private Populator<ItemOut, AbstractOrderEntryModel> orderEntryOverridingPopulator;
	private Populator<OrderRequestHeader, CartModel> orderRequestCartPopulator;
	private PunchOutSessionService punchOutSessionService;


	public void process(final CXML input, final CartModel output)
	{
		final OrderRequest orderRequest = getOrderRequest(input);
		final OrderRequestHeader orderRequestHeader = orderRequest.getOrderRequestHeader();
		if (orderRequest.getItemOut() == null || orderRequest.getItemOut().isEmpty())
		{
			throw new PunchOutException(HttpStatus.BAD_REQUEST, "Miss ItemOut in cxml request.");
		}

		if (!getPunchOutSessionService().isPunchOutSessionCartValid().booleanValue())
		{
			for (final ItemOut itemOut : orderRequest.getItemOut())
			{
				final AbstractOrderEntryModel orderEntryModel = getItemOutConverter().convert(itemOut);

				getOrderEntryOverridingPopulator().populate(itemOut, orderEntryModel);
			}
		}
		// populating the cart at the end so that we are able to override the total price, taxes, etc.
		getOrderRequestCartPopulator().populate(orderRequestHeader, output);
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

	protected Converter<ItemOut, AbstractOrderEntryModel> getItemOutConverter()
	{
		return itemOutConverter;
	}

	public void setItemOutConverter(final Converter<ItemOut, AbstractOrderEntryModel> itemOutConverter)
	{
		this.itemOutConverter = itemOutConverter;
	}

	protected Populator<ItemOut, AbstractOrderEntryModel> getOrderEntryOverridingPopulator()
	{
		return orderEntryOverridingPopulator;
	}

	public void setOrderEntryOverridingPopulator(final Populator<ItemOut, AbstractOrderEntryModel> orderEntryOverridingPopulator)
	{
		this.orderEntryOverridingPopulator = orderEntryOverridingPopulator;
	}

	protected Populator<OrderRequestHeader, CartModel> getOrderRequestCartPopulator()
	{
		return orderRequestCartPopulator;
	}

	public void setOrderRequestCartPopulator(final Populator<OrderRequestHeader, CartModel> orderRequestCartPopulator)
	{
		this.orderRequestCartPopulator = orderRequestCartPopulator;
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
