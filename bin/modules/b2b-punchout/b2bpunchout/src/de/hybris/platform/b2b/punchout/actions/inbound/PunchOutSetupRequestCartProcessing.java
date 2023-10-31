/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import de.hybris.platform.b2b.punchout.services.CXMLElementBrowser;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.cxml.CXML;
import org.cxml.ItemOut;
import org.cxml.PunchOutSetupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

/**
 * Handles operation="edit" onto the {@link PunchOutSetupRequest} by populating the session shopping cart.
 */
public class PunchOutSetupRequestCartProcessing
{
	private static final Logger LOG = LoggerFactory.getLogger(PunchOutSetupRequestCartProcessing.class);

	private Converter<ItemOut, AbstractOrderEntryModel> itemOutConverter;
	private CartFacade cartFacade;
	private ModelService modelService;

	public void processCartData(final CXML input)
	{
		final CXMLElementBrowser cxmlElementBrowser = new CXMLElementBrowser(input);
		final PunchOutSetupRequest punchOutSetUpRequest = cxmlElementBrowser.findRequestByType(PunchOutSetupRequest.class);
		// clean old cart and create a new one
		getCartFacade().removeSessionCart();
		processItemOutAndAddToSessionCart(punchOutSetUpRequest.getItemOut());
		//recalculate the cart and save our changes in session cart
		getCartFacade().update(getCartFacade().getCurrentCart());
	}

	/**
	 * Populates a shopping cart using the provided list of {@link ItemOut} elements.
	 *
	 * @param itemsOut all the items out
	 */
	protected void processItemOutAndAddToSessionCart(final List<ItemOut> itemsOut)
	{
		if (CollectionUtils.isEmpty(itemsOut))
		{
			LOG.debug("No items to fill in cart");
			return;
		}

		final List<AbstractOrderEntryModel> cartEntryModels = itemsOut.stream()
				.map(itemOut -> getItemOutConverter().convert(itemOut))
				.collect(Collectors.toList());
		getModelService().saveAll(cartEntryModels);
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	protected Converter<ItemOut, AbstractOrderEntryModel> getItemOutConverter()
	{
		return itemOutConverter;
	}

	public void setItemOutConverter(final Converter<ItemOut, AbstractOrderEntryModel> itemOutConverter)
	{
		this.itemOutConverter = itemOutConverter;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
