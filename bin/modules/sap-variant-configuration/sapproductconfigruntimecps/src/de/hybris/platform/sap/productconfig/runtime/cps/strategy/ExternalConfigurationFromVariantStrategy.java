/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.strategy;

import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;


/**
 * Creating an external configuration from a product variant's classification
 */
public interface ExternalConfigurationFromVariantStrategy
{

	/**
	 * Creates an external configuration that corresponds to the variant's characteristic value assignments
	 *
	 * @param productcode
	 *           Product code of variant
	 * @param kbId
	 *           ID of current knowledge base
	 * @return Configuration in external format (can be used for creating a runtime configuration)
	 */
	CPSExternalConfiguration createExternalConfiguration(String productcode, String kbId);

}
