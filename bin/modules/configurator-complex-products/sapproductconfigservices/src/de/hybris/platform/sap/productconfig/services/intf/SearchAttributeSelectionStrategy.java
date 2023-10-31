/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.intf;

import de.hybris.platform.commerceservices.search.solrfacetsearch.strategies.exceptions.NoValidSolrConfigException;

import java.util.Set;


/**
 * Determines search attributes
 */
public interface SearchAttributeSelectionStrategy
{

	/**
	 * Is there an indexed attribute with a specific name?
	 *
	 * @param attributeName
	 * @return True if attribute is available on search index
	 * @throws NoValidSolrConfigException
	 */
	boolean isAttributeAvailableOnSearchIndex(String attributeName, Set<String> solrIndexedProperties);

	Set<String> compileIndexedProperties() throws NoValidSolrConfigException;

}
