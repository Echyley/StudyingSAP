/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf;

import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ProductConfigurationRelatedObjectType;


/**
 * Handles product configurations when deep copying abstract order entries. Decides if it's needed to clone
 * configurations.
 */
public interface ConfigurationDeepCopyHandler
{

	/**
	 * Deep Copies a configuration. The implementation can decide if a deep copy is needed; if not, the input ID is
	 * simply returned.
	 *
	 * @param configId
	 *           ID of existing configuration
	 * @param externalConfiguration
	 *           optional - externalConfiguration, if provided this will be used as source, instead of obtaining the
	 *           external config via the provided configId
	 * @param productCode
	 *           optional - product code of configurable product to be copied
	 * @param force
	 *           if <code>true</code> a deep copy is enforced
	 * @param targetObjectType
	 *           the order entry object type for which the deep copy is executed
	 * @return ID of new configuration if a deep copy was performed; input otherwise
	 */
	String deepCopyConfiguration(final String configId, final String productCode, final String externalConfiguration,
			boolean force, final ProductConfigurationRelatedObjectType targetObjectType);
}
