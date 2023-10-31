/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.intf;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.services.strategies.impl.ProductConfigurationCartEntryValidationStrategyImpl;


/**
 * Validation strategy for configurable cart entries.
 */
public interface ProductConfigurationCartEntryValidationStrategy
{
	/**
	 * Validates a cart entry model with regards to product configuration
	 *
	 * @param cartEntryModel
	 *           Model representation of cart entry
	 * @return Null if no issue occurred. A modification in status
	 *         {@link ProductConfigurationCartEntryValidationStrategyImpl#REVIEW_CONFIGURATION} in case a validation
	 *         error occurred.
	 */
	CommerceCartModification validateConfiguration(final AbstractOrderEntryModel cartEntryModel);
}
