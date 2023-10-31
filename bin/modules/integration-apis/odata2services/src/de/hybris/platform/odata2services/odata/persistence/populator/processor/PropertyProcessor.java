/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.persistence.populator.processor;

import de.hybris.platform.odata2services.odata.persistence.ItemConversionRequest;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;

/**
 * Responsible for populating an {@code ODataEntry} from an {@code ItemModel}.
 */
public interface PropertyProcessor
{
	/**
	 * Populates properties of the specified {@code ODataEntry}.
	 * @param oDataEntry an entry to be populated from an {@link de.hybris.platform.core.model.ItemModel}
	 * @param request a request carrying all context information needed for correct population of the entry by a specific
	 * implementation of the property populator.
	 * @throws EdmException when unexpected problems occur while accessing the EDM info.
	 */
	void processEntity(ODataEntry oDataEntry, ItemConversionRequest request) throws EdmException;
}
