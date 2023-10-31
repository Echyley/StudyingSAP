/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.persistence;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;

/**
 * Service to create or update platform items based on an ODataEntry
 */
public interface ModelEntityService
{
	/**
	 * Converts an itemModel into a ODataEntry
	 * @param conversionRequest Parameter object that holds values for getting an ODataEntry
	 * @return The ODataEntry representation
	 * @throws EdmException in case there is an OData related issue
	 */
	ODataEntry getODataEntry(ItemConversionRequest conversionRequest) throws EdmException;

	/**
	 * Counts how many items in the platform match the provided request conditions.
	 * @param lookupRequest a request specifying an item type, at a minimum, and possibly other conditions. For example, the
	 * request may point to the objects nested in the request's base item type and referred by the navigation segments.
	 * @return number of items in the platform matching the request conditions.
	 * @throws EdmException in case there is an OData related issue
	 */
	int count(ItemLookupRequest lookupRequest) throws EdmException;
}
