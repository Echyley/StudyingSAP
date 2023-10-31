/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;


/**
 * This strategy is responsible for removing the link between cart bound configurations and products
 */
public interface ConfigurationReleaseProductLinkStrategy
{
	/**
	 * Releases the product relation of the configuration that is attached to the given abstract order entry
	 *
	 * @param abstractOrderEntry
	 */
	void releaseCartEntryProductRelation(AbstractOrderEntryModel abstractOrderEntry);

}

