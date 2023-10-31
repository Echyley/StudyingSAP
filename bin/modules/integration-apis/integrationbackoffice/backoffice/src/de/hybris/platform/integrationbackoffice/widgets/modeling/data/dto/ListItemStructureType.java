/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto;

/**
 * Indicates the data structure type of an attribute.
 */
public enum ListItemStructureType
{
	/**
	 * Indicates that the attribute is a collection.
	 */
	COLLECTION,
	/**
	 * Indicates that the attribute is a map.
	 */
	MAP,
	/**
	 * Indicates that the attribute isn't a data structure.
	 */
	NONE
}
