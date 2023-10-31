/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades;

/**
 * Accessing the session to set and read product configuration related entities like UIStatus or runtime configuration
 * ID per cart entry
 */
public interface SessionAccessFacade
{


	/**
	 * Retrieves object from the session for a given cart entry key
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @return T which represents the UiStatus
	 */
	<T> T getUiStatusForCartEntry(String cartEntryKey);

	/**
	 * Stores object for a cart entry key into the session
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @param uiStatus
	 *           the status of the UI
	 */
	void setUiStatusForCartEntry(String cartEntryKey, Object uiStatus);

	/**
	 * Stores object for a product key into the session
	 *
	 * @param productKey
	 *           Product key
	 * @param uiStatus
	 *           the status of the UI
	 */
	void setUiStatusForProduct(String productKey, Object uiStatus);

	/**
	 * Retrieves object from the session for a given cart entry key
	 *
	 * @param productKey
	 *           Product key
	 * @return T which represents the UiStatus
	 */
	<T> T getUiStatusForProduct(String productKey);

	/**
	 * Removes object for a cart entry
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 */
	void removeUiStatusForCartEntry(String cartEntryKey);

	/**
	 * Removes object for a product
	 *
	 * @param productKey
	 *           Product key
	 */
	void removeUiStatusForProduct(String productKey);

}
