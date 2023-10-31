/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.model.cronjob;


/**
 * Encapsulation of accesses to properties file for SSC DB attributes
 */
public interface PropertyAccessFacade
{

	/**
	 * @return true if the delta load has to be started automatically after the initial load
	 */
	boolean getStartDeltaloadAfterInitial();

}
