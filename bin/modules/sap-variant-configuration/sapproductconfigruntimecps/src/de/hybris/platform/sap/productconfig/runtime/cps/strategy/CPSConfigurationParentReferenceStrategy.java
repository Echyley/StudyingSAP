/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.strategy;

import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;


/**
 * Strategy for adding parent reference within {@link CPSConfiguration}
 */
public interface CPSConfigurationParentReferenceStrategy
{
	/**
	 * Adds parent references to passed runtime configuration
	 *
	 * @param cpsConfig
	 *           runtime configuration
	 */
	void addParentReferences(final CPSConfiguration cpsConfig);

	/**
	 * Add parent references to item
	 * 
	 * @param cpsItem
	 *           CPS item
	 */
	void addParentReferences(final CPSItem cpsItem);


}
