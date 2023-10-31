/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util.files;

import java.util.Collection;

/**
 * The XML interface that extracts specified items from an xml file
 */
public interface ItemsXml
{
	/**
	 * Retrieves all type codes, which directly or indirectly present in this item XML file
	 *
	 * @return type codes for complex type, enums, map types, etc., which are defined in this item XML and also all type codes
	 * for the types extended by the items in this items XML. If this file is empty, then an empty collection is returned;
	 */
	Collection<String> getContextTypeCodes();

	/**
	 * Retrieves all type codes, which explicitly present and created in this file. If a type is present but simply adds more
	 * attributes to a type that was created in a different file, then it's excluded from the type codes returned by this method.
	 *
	 * @return a collection of item type codes defined in this items.xml file or an empty collection, if this file is empty.
	 */
	Collection<String> getCreatedTypeCodes();

	/**
	 * Retrieves item types defined in this file.
	 *
	 * @return all item types contained in this items.xml or an empty collection, if this file does not contain complex item types.
	 */
	Collection<ItemType> getItemTypes();

	/**
	 * Checks whether the specified item type attribute is present in the context of this items.xml file.
	 *
	 * @param itemType      type code of the item that should contain the specified attribute. It may be not only item type
	 *                      explicitly defined in this file but also an item type inherited by an item definition in this file or
	 *                      an item type from referenced in a relation in this file, etc.
	 * @param attributeName name of the attribute to check for the existence in the specified item type. The attribute may be
	 *                      defined in this file or used in a relation or in an inherited item type, etc.
	 * @return {@code true}, if the specified item contains the attribute; {@code false}, if either the item is not found for the
	 * specified {@code itemType} or the item does not contain the specified attribute.
	 */
	boolean containsAttribute(String itemType, String attributeName);
}
