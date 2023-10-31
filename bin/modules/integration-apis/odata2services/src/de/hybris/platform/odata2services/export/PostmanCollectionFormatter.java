/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export;

import de.hybris.platform.odata2services.export.impl.PostmanCollection;

/**
 * Formats a Postman collection object.
 */
public interface PostmanCollectionFormatter
{
	/**
	 * Formats A Postman collection as a Json string.
	 *
	 * @param postmanCollection the {@link PostmanCollection} object to format
	 * @return a Json representation of the Postman collection
	 */
	String format(PostmanCollection postmanCollection);
}
