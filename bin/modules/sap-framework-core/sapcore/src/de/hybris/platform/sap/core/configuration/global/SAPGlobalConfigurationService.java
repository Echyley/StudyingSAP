/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.global;

import de.hybris.platform.sap.core.configuration.ConfigurationPropertyAccess;


/**
 * Interface to read global configuration properties.
 */
public interface SAPGlobalConfigurationService extends ConfigurationPropertyAccess
{
	/**
	 * Returns true if a SAPGlobalConfiguration exists.
	 *
	 * @return true if SAPGlobalConfiguration exists
	 */
	public boolean sapGlobalConfigurationExists();
}
