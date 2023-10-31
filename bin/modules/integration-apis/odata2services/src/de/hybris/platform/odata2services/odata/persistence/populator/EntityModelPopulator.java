/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.populator;

import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;

/**
 * Converts and populates Items based on EdmEntity information.
 */
public interface EntityModelPopulator
{
	/**
	 * Populate oDataEntry with the given info
	 * @param oDataEntry The OData entry
	 * @param conversionRequest the request context
	 * @throws EdmException -
	 */
	void populateEntity(ODataEntry oDataEntry, ItemConversionRequest conversionRequest) throws EdmException;
}
