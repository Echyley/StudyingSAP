/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services;

/**
 * Accessing the session to set and read product configuration related entities like UIStatus or runtime configuration
 * ID per cart entry
 */
//Refactoring the constants below into an Enum or own class would be a incompatible change, which we want to avoid.
public interface SessionAccessService
{
	/**
	 * cache key of product configuration cache container
	 */
	String PRODUCT_CONFIG_SESSION_ATTRIBUTE_CONTAINER = "productconfigSessionAttributeContainer";

	/**
	 * Stores configuration ID for a cart entry key into the session
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @param configId
	 *           ID of a runtime configuration object
	 */
	void setConfigIdForCartEntry(String cartEntryKey, String configId);

	/**
	 * Retrieves config identifier from the session for a given cart entry key
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @return ID of a runtime configuration object
	 */
	String getConfigIdForCartEntry(String cartEntryKey);

	/**
	 * Retrieves object from the session for a given cart entry key
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @return ui status for cart entry
	 */
	<T> T getUiStatusForCartEntry(String cartEntryKey);

	/**
	 * Retrieves object from the session for a given cart entry key
	 *
	 * @param productKey
	 *           Product key
	 * @return ui status for product
	 */
	<T> T getUiStatusForProduct(String productKey);

	/**
	 * Stores object for a cart entry key into the session
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @param uiStatus
	 *           ui status for cart entry
	 */
	void setUiStatusForCartEntry(String cartEntryKey, Object uiStatus);

	/**
	 * Stores object for a product key into the session
	 *
	 * @param productKey
	 *           Product key
	 * @param uiStatus
	 *           ui status for product
	 */
	void setUiStatusForProduct(String productKey, Object uiStatus);

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



	/**
	 * Retrieves cart entry key belonging to a specific config ID
	 *
	 * @param configId
	 *           id of the configuration
	 * @return String representation of the cart entry primary key
	 */
	String getCartEntryForConfigId(String configId);

	/**
	 * Removes config ID for cart entry
	 *
	 * @param cartEntryKey
	 *           cart entry key
	 */
	void removeConfigIdForCartEntry(String cartEntryKey);

	/**
	 * Removes all session artifacts belonging to a cart entry
	 *
	 * @param cartEntryId
	 *           cart entry key
	 * @param productKey
	 *           product key
	 */
	void removeSessionArtifactsForCartEntry(String cartEntryId);

	/**
	 * Purges the entire session (with regards to CPQ artifacts)
	 */
	void purge();

	/**
	 * Get the runtime configuration currently associated with the given product
	 *
	 * @param productCode
	 *           code of product, for which the link to the runtime configuration should be returned
	 * @return runtime configuration id that is currently linked to the given product
	 */
	String getConfigIdForProduct(String productCode);

	/**
	 * Get the product for the given runtime configuration id
	 *
	 * @param configId
	 *           configuration id
	 * @return product code if link is present, otherwise null
	 */
	String getProductForConfigId(String configId);

	/**
	 * Links a product code with a given runtime configuration
	 *
	 * @param productCode
	 *           code of product, for which the link to the runtime configuration should be created
	 * @param configId
	 *           runtime configuration id
	 */
	void setConfigIdForProduct(String productCode, String configId);

	/**
	 * Removes the link between product code and runtime configuration
	 *
	 * @param productCode
	 *           code of product, for which the link to the runtime configuration should be deleted
	 */
	void removeConfigIdForProduct(String productCode);

	/**
	 * get cart entry linked to the given draft configuration
	 *
	 * @param configId
	 *           runtime configuration id
	 * @return cartItemHandle
	 */
	String getCartEntryForDraftConfigId(String configId);

	/**
	 * gets the config id linked as draft to the given cart entry
	 *
	 * @param cartEntryKey
	 *           cart entry key
	 * @return config Id
	 */
	String getDraftConfigIdForCartEntry(String cartEntryKey);

	/**
	 * Stores darfat configuration ID for a cart entry key into the session
	 *
	 * @param cartEntryKey
	 *           String representation of the cart entry primary key
	 * @param configId
	 *           ID of a runtime configuration object
	 */
	void setDraftConfigIdForCartEntry(String cartEntryKey, String configId);

	/**
	 * Removes draft config ID for cart entry
	 *
	 * @param cartEntryKey
	 *           cart entry key
	 */
	void removeDraftConfigIdForCartEntry(String cartEntryKey);

}
