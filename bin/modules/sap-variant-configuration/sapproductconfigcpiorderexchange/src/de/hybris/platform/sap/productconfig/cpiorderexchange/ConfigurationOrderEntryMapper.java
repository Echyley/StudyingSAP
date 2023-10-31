/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.cpiorderexchange;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;


/**
 * Enriches the order with the configuration details of the given order entry
 */
public interface ConfigurationOrderEntryMapper
{
	/**
	 * Enriches the order with the configuration details of the given order entry
	 *
	 * @param entry
	 *           order entry
	 * @param orderModel
	 *           outbound order model to be enriched
	 * @param entryNumber
	 *           entry number for the outbound order entry
	 * @return number of instances in the bom of the configurable product
	 */
	int mapConfiguration(final AbstractOrderEntryModel entry, final SAPCpiOutboundOrderModel orderModel, final String entryNumber);

	/**
	 * Provides information whether call to this mapper is required
	 *
	 * @param entry
	 *           order entry to be checked
	 * @return whether call to this mapper is necessary
	 */
	boolean isMapperApplicable(final AbstractOrderEntryModel entry);
}
