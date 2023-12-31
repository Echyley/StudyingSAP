/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf;

import de.hybris.platform.core.model.order.AbstractOrderModel;


/**
 * strategy to handle coping/cloning of product configurations
 */
public interface ConfigurationCopyStrategy
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
	 * @return ID of new configuration if a deep copy was performed; input otherwise
	 */
	String deepCopyConfiguration(final String configId, final String productCode, final String externalConfiguration,
			boolean force);

	/**
	 * Hook to be called after a document with a configuration attached was cloned. It might be necessary to clone the
	 * respective configuration, as well. This happens typically were the configuration is stored in an external system
	 * and only reference in hybris (CPS). In case the configuration is attached in an external serialized form, no more
	 * actions are necessary (SSC).
	 *
	 * @param source
	 *           source document
	 * @param target
	 *           target document
	 */
	void finalizeClone(AbstractOrderModel source, AbstractOrderModel target);

	/**
	 * Hook to be called after a document with a configuration attached was cloned. It might be necessary to copy (no
	 * deep copy) the respective configuration, as well. This happens typically were the configuration is stored in an
	 * external system and only reference in hybris (CPS). In case the configuration is attached in an external
	 * serialized form, no more actions are necessary (SSC).
	 *
	 * @param source
	 *           source document
	 * @param target
	 *           target document
	 */
	void finalizeCloneShallowCopy(AbstractOrderModel source, AbstractOrderModel target);
}
