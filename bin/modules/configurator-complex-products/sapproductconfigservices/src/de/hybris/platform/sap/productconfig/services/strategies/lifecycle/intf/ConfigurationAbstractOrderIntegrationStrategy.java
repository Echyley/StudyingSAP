/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


/**
 * This strategy manages the integration between carts, orders, quotes and product configuration.
 */
public interface ConfigurationAbstractOrderIntegrationStrategy
{

	/**
	 * Update abstract order entry on link step
	 *
	 * @param parameters
	 * @param entry
	 */
	void updateAbstractOrderEntryOnLink(final CommerceCartParameter parameters, final AbstractOrderEntryModel entry);

	/**
	 * Update abstract order entry on link step
	 *
	 * @param parameters
	 * @param entry
	 */
	void updateAbstractOrderEntryOnUpdate(String configId, final AbstractOrderEntryModel entry);

	/**
	 * Get configuration attached to a cart, quote or order entry
	 *
	 * @param entry
	 * @return Configuration runtime representation
	 */
	ConfigModel getConfigurationForAbstractOrderEntry(final AbstractOrderEntryModel entry);

	/**
	 * Get configuration attached to a cart, quote or order entry for a one time access
	 *
	 * @param entry
	 * @return Configuration runtime representation
	 */
	ConfigModel getConfigurationForAbstractOrderEntryForOneTimeAccess(final AbstractOrderEntryModel entry);

	/**
	 * Get external configuration attached to a cart, quote or order entry
	 *
	 * @param entry
	 * @return Configuration in external format
	 */
	String getExternalConfigurationForAbstractOrderEntry(final AbstractOrderEntryModel entry);

	/**
	 * Do we have a KB version corresponding to an abstract order entry?
	 *
	 * @param entry
	 * @return True if KB version exists
	 */
	boolean isKbVersionForEntryExisting(final AbstractOrderEntryModel entry);

	/**
	 * Removes links between cart entry, product and UI status
	 *
	 * @param entry
	 */
	void finalizeCartEntry(final AbstractOrderEntryModel entry);

	/**
	 * Invalidates the configuration attached to a cart entry. Forces the creation of a default configuration attached to
	 * the cart entry, replacing the invalid one
	 *
	 * @param entry
	 */
	void invalidateCartEntryConfiguration(final AbstractOrderEntryModel entry);

	/**
	 * Prepares entry for order replication
	 *
	 * @param entry
	 */
	void prepareForOrderReplication(final AbstractOrderEntryModel entry);


	/**
	 * Checks whether a runtimne configuration exists for the given entry. For example if a variant product was ordered
	 * diretly, no runtime configuration may exist.
	 *
	 * @param entry
	 * @return True if RT configuration exists
	 */
	boolean isRuntimeConfigForEntryExisting(AbstractOrderEntryModel entry);

	/**
	 * Refresh cart entry configuration
	 *
	 * @param entry
	 *           - abstract order entry
	 * @return configuration model
	 */
	default ConfigModel refreshCartEntryConfiguration(final AbstractOrderEntryModel entry)
	{
		return getConfigurationForAbstractOrderEntry(entry);
	}

}
