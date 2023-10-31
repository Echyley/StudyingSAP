/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.intf;

/**
 * Responsible for access control of runtime product configuration entities.
 */
public interface ProductConfigurationAccessControlService
{

	/**
	 * Check if a configuration update is allowed for a configuration specified by its ID
	 *
	 * @param configId
	 *           Configuration ID
	 * @return Allowed!
	 */
	boolean isUpdateAllowed(String configId);

	/**
	 * Check if reading configuration is allowed for a configuration specified by its ID
	 *
	 * @param configId
	 *           Configuration ID
	 * @return Allowed!
	 */
	boolean isReadAllowed(String configId);

	/**
	 * Check if a configuration release is allowed for a configuration specified by its ID
	 *
	 * @param configId
	 *           Configuration ID
	 * @return Allowed!
	 */
	boolean isReleaseAllowed(String configId);

}
