/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util.files;

import java.util.Collection;

/**
 * The localization interface to access and parse a locale file
 */
public interface LocalizedProperties
{
	/**
	 * Retrieves all properties in this file localizing item types.
	 *
	 * @return item type properties in this file or an empty collection, if this file does not contain item type properties.
	 */
	Collection<PropertyKey> getItemProperties();

	/**
	 * Retrieves all property keys for an item attribute localization in this file.
	 *
	 * @return a collection of all attribute properties in this file or an empty collection, if this file does not contain
	 * attribute properties.
	 */
	Collection<PropertyKey> getAttributeProperties();

	/**
	 * Retrieves all properties that do not match naming conventions accepted for this localization properties file.
	 *
	 * @return localization property keys that do not match the naming convention or an empty collection, if all properties match
	 * the naming conventions for this localization properties file or, if this file is empty and does not contain any
	 * localization properties.
	 */
	Collection<String> getPropertiesNotMatchingNamingConventions();

	/**
	 * Verifies if there is a key in the relevant property file for the specified item type.
	 *
	 * @param itemType the item type which we are verifying that exists in the properties file
	 * @return whether the key is present or not in the file
	 */
	boolean keyExistsForItem(String itemType);

	/**
	 * Verifies if there is a key in the relevant property file for the specified item type attribute.
	 *
	 * @param itemType  the item type of the attribute which we are looking for
	 * @param attribute the attribute which we are verifying that exists in the properties file
	 * @return whether the attribute is present or not in the file
	 */
	boolean keyExistsForAttribute(String itemType, String attribute);

	/**
	 * Retrieves all properties with names following the naming conventions for this localization file but not having values.
	 *
	 * @return a collection of properties without localized values or an empty collection, if all properties are localized or this
	 * file is empty.
	 */
	Collection<String> getValidPropertiesWithoutValues();
}
