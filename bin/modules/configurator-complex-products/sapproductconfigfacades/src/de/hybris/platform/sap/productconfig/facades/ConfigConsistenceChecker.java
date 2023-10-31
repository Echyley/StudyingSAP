/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades;


/**
 * Callback-Interface for manipulating the product configuration data transfer objects after they have been updated from
 * the model.
 */
public interface ConfigConsistenceChecker
{

	/**
	 * This method will be called after the product configuration DAO has been updated from the model.
	 *
	 * @param configData
	 *           original DAO
	 */
	void checkConfiguration(ConfigurationData configData);
}
