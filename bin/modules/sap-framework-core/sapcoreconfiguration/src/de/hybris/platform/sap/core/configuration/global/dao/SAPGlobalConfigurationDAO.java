/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.global.dao;

import de.hybris.platform.sap.core.configuration.model.SAPGlobalConfigurationModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;


/**
 * Interface for accessing the data of the global configuration.
 */
public interface SAPGlobalConfigurationDAO
{

	/**
	 * Finds the {@link SAPGlobalConfigurationModel} by performing a FlexibleSearch using the
	 * {@link FlexibleSearchService}.
	 * 
	 * @return {@link SAPGlobalConfigurationModel}
	 */
	public SAPGlobalConfigurationModel getSAPGlobalConfiguration();
}
