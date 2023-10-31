/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.cpq.productconfig.facades.data.ProductConfigData;


/**
 * Facade for integration between the shopping cart and CPQ configurable products. <br>
 */
public interface CartIntegrationFacade
{
	/**
	 * Adds the current CPQ configuration to shopping cart.
	 *
	 * @param configId
	 *           configuration id to add to the shopping cart
	 * @param quantity
	 *           quantity of the cart entry
	 * @return cart modification data containing modified cart entry
	 * @throws CommerceCartModificationException
	 *            in case adding to cart failed
	 */
	CartModificationData addConfigurationToCart(final String configId, long quantity) throws CommerceCartModificationException;

	/**
	 * Gets product code that is assigned to the cart entry specified by the entry number provided
	 *
	 * @param entryNumber
	 *           Cart entry number
	 * @return product code
	 */
	String getProductCodeForSessionCartEntry(final Integer entryNumber);

	/**
	 * Gets configuration ID that is assigned to the cart entry specified by the entry number provided
	 *
	 * @param entryNumber
	 *           Cart entry number
	 * @return ProductConfigData containing the CPQ configuration ID
	 */
	ProductConfigData getConfigIdForSessionCartEntry(Integer entryNumber);

	/**
	 * Updates cart entry (prices, configuration summary) from the configuration attached to it
	 *
	 * @param configId
	 *           Configuration identifier
	 * @param cartEntryNumber
	 *           Cart entry number
	 * @return cart modification data containing modified cart entry
	 */
	CartModificationData updateCartEntryFromConfiguration(String configId, int cartEntryNumber);
}
