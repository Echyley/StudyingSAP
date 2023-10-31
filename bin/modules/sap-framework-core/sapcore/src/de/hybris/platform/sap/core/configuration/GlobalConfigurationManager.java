/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration;

import java.util.Set;



/**
 * Interface to access the global configuration information.
 * <p>
 * It collects
 * <ul>
 * <li>all sap module ids which are defined in the extensions</li>
 * <li>all extensions names which are related to the module ids</li>
 * </ul>
 * The sap module ids are defined in the project.properties file of in the following format
 * {@code <extensionName>.sap.moduleId=<moduleId>}.
 * </p>
 */
public interface GlobalConfigurationManager
{

	/**
	 * Returns all registered module ids.
	 * 
	 * @return module id array
	 */
	public Set<String> getModuleIds();

	/**
	 * Returns the extension names for the requested module id.
	 * 
	 * @param moduleId
	 *           requested module id
	 * @return set of extension names
	 */
	public Set<String> getExtensionNames(String moduleId);

}
