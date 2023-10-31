/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.eventtracking.services.populators;

import de.hybris.eventtracking.model.events.AbstractTrackingEvent;
import de.hybris.eventtracking.model.events.AddToCartEvent;
import de.hybris.eventtracking.services.constants.TrackingEventJsonFields;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import com.fasterxml.jackson.databind.ObjectMapper; 
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;



/**
 * @author stevo.slavic
 *
 */
public class AddToCartEventPopulator extends AbstractTrackingEventGenericPopulator
{

	private static final int CART_ITEM_DATA_THRESHOLD = 3;
	private static final String DEFAULT_QUANTITY = "0";
	private static final int CART_QUANTITY_INDEX = 4;

	public AddToCartEventPopulator(final ObjectMapper mapper)
	{
		super(mapper);
	}

	/**
	 * @see de.hybris.eventtracking.services.populators.GenericPopulator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> clazz)
	{
		return AddToCartEvent.class.isAssignableFrom(clazz);
	}

	/**
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final Map<String, Object> trackingEventData, final AbstractTrackingEvent trackingEvent)
			throws ConversionException
	{

		final String productSku = ((AddToCartEvent) trackingEvent).getProductId();
		final Map<String, Object> customVariablesPageScoped = getPageScopedCvar(trackingEventData);
		
		String quantity = DEFAULT_QUANTITY;
		
		String cartId = null;
		
		if (customVariablesPageScoped != null)
		{
			final List<String> cvrIndex1 = (List<String>) customVariablesPageScoped.get("1");
			if (cvrIndex1 != null && cvrIndex1.size() > 1)
			{
				cartId = cvrIndex1.get(1);
			}
		}
		
		if (StringUtils.isNotBlank(productSku))
		{
			final String ecItems = (String) trackingEventData.get(TrackingEventJsonFields.COMMERCE_CART_ITEMS.getKey());
			List<List<Object>> cartItemsData = null;
			if (StringUtils.isNotBlank(ecItems))
			{
				try
				{
					cartItemsData = getMapper().readValue(ecItems, List.class);
				}
				catch (final IOException e)
				{
					throw new ConversionException("Error reading event data", e);
				}
			}

			if (cartItemsData != null)
			{
				quantity = getQuantity(cartItemsData, productSku);
			}
		}


		trackingEvent.setEventType("AddToCartEvent");
		((AddToCartEvent) trackingEvent).setQuantity(quantity);
		((AddToCartEvent) trackingEvent).setCartId(cartId);
		
	}

	private static boolean isCartItemDataNotBlank(List<Object> cartItemData) {
		return cartItemData != null && cartItemData.size() > CART_ITEM_DATA_THRESHOLD;
	}

	private static String getQuantity(final List<List<Object>> cartItemsData, final String productSku) {
		for (final List<Object> cartItemData : cartItemsData) {
			if (isCartItemDataNotBlank(cartItemData) && (productSku.equals(cartItemData.get(0)))) {
				return (String) cartItemData.get(CART_QUANTITY_INDEX);
			}
		}
		return DEFAULT_QUANTITY;
	}

}
