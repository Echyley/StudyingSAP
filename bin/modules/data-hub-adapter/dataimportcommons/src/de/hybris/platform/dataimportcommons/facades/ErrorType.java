/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.dataimportcommons.facades;

/**
 * Defines type of an import error.
 */
public enum ErrorType
{
	/**
	 * Indicates an error with the impex header, which results in all items of that section not being imported.
	 */
	HEADER,
	/**
	 * Indicates that an item attribute value references non-existing enum value or an attribute in another item.
	 */
	REFERENCE,
	/**
	 * Indicates invalid value for an item attribute value.
	 */
	ATTRIBUTE_VALUE;
}
