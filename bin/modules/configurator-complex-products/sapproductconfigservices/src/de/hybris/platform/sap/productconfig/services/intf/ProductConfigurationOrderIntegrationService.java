/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.intf;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.data.CartEntryConfigurationAttributes;


/**
 * Facilitates interaction between configuration, pricing and order entries.
 *
 */
public interface ProductConfigurationOrderIntegrationService
{

	/**
	 * Calculates configuration relevant attributes at cart entry level
	 *
	 * @param model
	 *           Cart Entry
	 * @return attributes relevant for configuration
	 */
	CartEntryConfigurationAttributes calculateCartEntryConfigurationAttributes(AbstractOrderEntryModel model);

	/**
	 * Updates cart entry's external configuration and creates configuration in current session from external string
	 * representation (which contains the configuration in XML format)
	 *
	 * @param externalConfiguration
	 *           Configuration as XML string
	 * @param entry
	 *           cart entry
	 * @return true if cart entry has been updated
	 */
	boolean updateCartEntryExternalConfiguration(final String externalConfiguration, final AbstractOrderEntryModel entry);

	/**
	 * Update the product of the cartItem, if the product is different to the current cart item product
	 *
	 * @param entry
	 *           Entry to change, if necessary
	 * @param product
	 *           cart item product
	 * @param configId
	 *           ID of the current configuration
	 * @return true if the entry was updated
	 */
	boolean updateCartEntryProduct(final AbstractOrderEntryModel entry, final ProductModel product, final String configId);

	/**
	 * Fill the summary map at the order entry with configuration status information
	 *
	 * @param entry
	 *           Entry to be enhanced with additional information
	 */
	void fillSummaryMap(final AbstractOrderEntryModel entry);


	/**
	 * @deprecated since 18.08
	 */
	@Deprecated(since = "1808", forRemoval = true)
	public CartEntryConfigurationAttributes calculateCartEntryConfigurationAttributes(final String cartEntryKey,
			final String productCode, final String externalConfiguration);

}
