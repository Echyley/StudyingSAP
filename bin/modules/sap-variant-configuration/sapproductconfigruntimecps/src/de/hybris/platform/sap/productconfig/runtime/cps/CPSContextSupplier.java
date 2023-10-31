/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps;

import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.common.CPSContextInfo;

import java.util.List;


/**
 * Provide context for configuration service calls.
 */
public interface CPSContextSupplier
{
	/**
	 * Retrieves context for configuration service calls
	 * 
	 * @param productCode
	 *           Code of product in internal SAP format
	 *
	 * @return configuration context
	 */
	List<CPSContextInfo> retrieveContext(String productCode);
}
