/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.backend.interf;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchFilter;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.search.interf.SearchResultList;


/**
 * 
 */
public interface SearchBackend extends BackendBusinessObject
{
	/**
	 * Performs a search in backend, according to the filter settings
	 * 
	 * @param searchFilter
	 *           Filter settings
	 * @return Search result list
	 * @throws BackendException
	 */
	SearchResultList getSearchResult(SearchFilter searchFilter) throws BackendException;

	/**
	 * Retrieves date range
	 * 
	 * @return Date range in days
	 */
	int getDateRangeInDays();
}
