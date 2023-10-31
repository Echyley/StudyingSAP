/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.module;

import de.hybris.platform.sap.core.configuration.SAPConfigurationService;


/**
 * Interface to access runtime module configuration data.
 */
public interface ModuleConfigurationAccess extends SAPConfigurationService
{

	/**
	 * Returns the module id of the module configuration access.
	 * 
	 * @return Module id
	 */
	public String getModuleId();

}
