/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.company;


import de.hybris.platform.b2bacceleratorservices.document.AccountSummaryDocumentQuery;
import de.hybris.platform.b2bacceleratorservices.document.criteria.DefaultCriteria;
import de.hybris.platform.b2bacceleratorservices.enums.DocumentStatus;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;


/**
 * Provides services for B2BDocument business logic/domain
 *
 */
public interface B2BDocumentService
{
	/**
	 * Finds all B2BDocumentModel filtered by the query. The resulting list only contains document associated to the current
	 * user's B2BUnit.
	 *
	 * @param query
	 *           paged document query
	 * @return result : a SeachPageData< B2BDocumentModel > containing documents.
	 */
	SearchPageData<B2BDocumentModel> findDocuments(final AccountSummaryDocumentQuery query);

	/**
	 * Return all open documents for a given B2B Unit.
	 *
	 * @param unit
	 *           the B2B unit.
	 * @return result : a SeachPageData< B2BDocumentModel > containing open documents.
	 */
	SearchResult<B2BDocumentModel> getOpenDocuments(final B2BUnitModel unit);

	/**
	 * Returns all open documents for a given documentMediaModel
	 *
	 * @param mediaModel
	 *           the media model
	 * @return result : a SeachPageData< B2BDocumentModel > containing open documents.
	 */
	SearchResult<B2BDocumentModel> getOpenDocuments(final MediaModel mediaModel);

	/**
	 * Deletes documents based on the supplied criteria.
	 *
	 * @param numberOfDays
	 *           elapsed days since the document media's creation time
	 * @param documentTypes
	 *           a list of document types
	 * @param documentStatuses
	 *           a list of document statuses
	 */
	void deleteB2BDocumentFiles(final int numberOfDays, final List<B2BDocumentTypeModel> documentTypes,
			final List<DocumentStatus> documentStatuses);


	/**
	 * Returns paged documents for the given unit
	 *
	 * @param b2bUnitCode
	 *           the unit code
	 * @param pageableData
	 *           the pageable data
	 * @param criteriaList
	 *           the list of criteria objects
	 * @return result : a SeachPageData< B2BDocumentModel > containing documents from the given unit.
	 */
	SearchPageData<B2BDocumentModel> getPagedDocumentsForUnit(final String b2bUnitCode, final PageableData pageableData,
			final List<DefaultCriteria> criteriaList);

	/**
	 * Returns a Document Model for a given unit, document identifier
	 * @param b2bUnitCode
	 * 			 the unit code
	 * @param documentNumber
	 * 			 the document identifier
	 * @return result : a B2BDocumentModel of the retrieved document
	 */
    B2BDocumentModel getDocumentByIdForUnit(String b2bUnitCode, String documentNumber);
}
