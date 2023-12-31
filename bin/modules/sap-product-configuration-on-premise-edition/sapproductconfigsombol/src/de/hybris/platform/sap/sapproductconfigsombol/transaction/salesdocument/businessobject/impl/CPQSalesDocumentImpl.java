/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductconfigsombol.transaction.salesdocument.businessobject.impl;

import de.hybris.platform.sap.core.common.TechKey;

import java.util.HashMap;
import java.util.Map;


/**
 * Provide configurationIDs for CPQ Hooks
 */
public class CPQSalesDocumentImpl
{
	private final Map<TechKey, String> configurationIDs = new HashMap<>();


	/**
	 * Returns the configuration ID from the session for a given item handle
	 *
	 * @param key
	 *           Item key
	 * @return Configuration ID
	 */
	public String getConfigId(final TechKey key)
	{
		return configurationIDs.get(key);
	}


	public void setConfigId(final TechKey key, final String configId)
	{
		final Map<TechKey, String> configIds = this.configurationIDs;
		configIds.put(key, configId);

	}

}
