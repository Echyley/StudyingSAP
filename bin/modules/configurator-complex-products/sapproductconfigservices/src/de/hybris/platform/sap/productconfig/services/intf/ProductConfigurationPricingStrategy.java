/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.intf;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.util.PriceValue;


/**
 * Facilitates pricing integration for configurable products
 */
public interface ProductConfigurationPricingStrategy
{
	/**
	 * Calculates the current base price for a given abstract order entry. Note that this price is derived from the
	 * configuration and may differ from the one persisted on the cart entry. In this case the cart entry price is
	 * outdated. ConfigId has to be present in current session for given cart entry to retrieve configuration model and
	 * prices.
	 *
	 * @param entry
	 *           entry which has an associated product configuration
	 * @return current base price
	 */
	public PriceValue calculateBasePriceForConfiguration(final AbstractOrderEntryModel entry);

	/**
	 * Updates cart entry's base price (in case it deviates from the current configuration price) from configuration
	 * model/pricing service. ConfigId has to be present in current session for given cart entry to retrieve
	 * configuration model. This includes recalculation and saving of the cart if entry prices were updated.
	 *
	 * @param entry
	 *           cart entry
	 * @param calculateCart
	 *           specifies whether cart is calculated on successful update
	 * @param passedParameter
	 *           parameters for recalculation of the cart
	 * @return true if cart entry has been updated
	 */
	boolean updateCartEntryPrices(final AbstractOrderEntryModel entry, final boolean calculateCart,
			final CommerceCartParameter passedParameter);

	/**
	 * Indicates whether there is a problem to obtain correct prices for cart/order
	 *
	 * @param configModel
	 *           configuration model that represents the runtime state of the configuration
	 * @return true if no prices can be obtained at the moment
	 */
	boolean isCartPricingErrorPresent(ConfigModel configModel);

}
