/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;


/**
 * Deals with abstract order integration
 */
public interface AbstractOrderIntegrationService
{

	/**
	 * Retrieve configuration ID for abstract order entry
	 *
	 * @param entry
	 *           Abstract order entry
	 * @return Configuration ID
	 */
	String getConfigIdForAbstractOrderEntry(AbstractOrderEntryModel entry);


	/**
	 * * Save new configuration ID for abstract order entry
	 *
	 * @param entry
	 *           Abstract order entry
	 * @param newConfigId
	 *           new configuration ID
	 *
	 */
	void setConfigIdForAbstractOrderEntry(AbstractOrderEntryModel entry, String newConfigId);
}
