/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorfacades.company;

import de.hybris.platform.b2bacceleratorfacades.data.AccountSummaryInfoData;
import de.hybris.platform.b2bacceleratorfacades.document.data.AttachmentData;
import de.hybris.platform.b2bacceleratorfacades.document.data.B2BAmountBalanceData;
import de.hybris.platform.b2bacceleratorfacades.document.data.B2BDocumentData;
import de.hybris.platform.b2bacceleratorfacades.document.data.B2BDocumentPaymentInfoData;
import de.hybris.platform.b2bacceleratorfacades.document.data.OrgDocumentListData;
import de.hybris.platform.b2bacceleratorservices.document.criteria.DefaultCriteria;
import de.hybris.platform.b2bacceleratorservices.document.criteria.FilterByCriteriaData;
import de.hybris.platform.b2bacceleratorservices.document.data.B2BDragAndDropData;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;
import java.util.Map;



/**
 *
 * Used to provide unique interface to all B2BDocuments business logic/domain
 *
 */
public interface B2BAccountSummaryFacade
{
	/**
	 * Finds documents filtered by given query.
	 *
	 * @param queryParameters
	 *           the criteria parameters used to build a query
	 * @return all {@link B2BDocumentData} filtered by the criteria parameters
	 */
	SearchPageData<B2BDocumentData> findDocuments(final Map<String, String> queryParameters);

	/**
	 * Gets the balance for a given B2B Unit.
	 *
	 * @param unit
	 *           the B2BUnit
	 * @return a B2BAmountBalanceData object.
	 */
	B2BAmountBalanceData getAmountBalance(final B2BUnitModel unit);

	/**
	 * Gets all document types.
	 *
	 * @return all document types: SearchResult<B2BDocumentTypeModel>
	 */
	SearchResult<B2BDocumentTypeModel> getAllDocumentTypes();

	/**
	 * Gets open documents for a given Media Model.
	 *
	 * @param mediaModel
	 *           the Media Model
	 *
	 * @return all B2BDocumentModel: SearchResult<B2BDocumentModel>
	 */
	SearchResult<B2BDocumentModel> getOpenDocuments(final MediaModel mediaModel);

	/**
	 *
	 *
	 * Returns a list of document payment info
	 *
	 * @param documentNumber
	 *           the document number identification
	 * @return {@link SearchResult} of type {@link B2BDocumentPaymentInfoData}
	 */
	List<B2BDocumentPaymentInfoData> getDocumentPaymentInfo(final String documentNumber);


	/**
	 *
	 * Applies a list of drag&drop actions
	 *
	 * @param lstActions
	 *           the List of Actions
	 */
	void applyDragAndDropActions(final List<B2BDragAndDropData> lstActions);


	/**
	 * Returns account summary for the given unit
	 *
	 * @param b2bUnitCode
	 *           the B2BUnit
	 * @return a {@link AccountSummaryInfoData} object.
	 */
	AccountSummaryInfoData getAccountSummaryInfoData(final String b2bUnitCode);

	/**
	 * Returns List of documents based on supplied criteria.
	 *
	 * @param b2bUnitCode
	 *           the B2B Unit Code
	 * @param pageableData
	 *           the Pageable Data
	 * @param filterByCriteriaData
	 *           the Filtering Criteria
	 * @param criteria
	 *           the Default Criteria
	 * @return result : a {@link SeachPageData} of type {@link B2BDocumentData} containing documents of the given unit &
	 *         criteria
	 */
	SearchPageData<B2BDocumentData> getPagedDocumentsForUnit(final String b2bUnitCode, final PageableData pageableData,
			final FilterByCriteriaData filterByCriteriaData, final DefaultCriteria criteria);

	/**
	 * Returns an OrgDocumentList of type B2BDocumentData based on supplied criteria
	 *
	 * @param b2bUnitCode the B2B Unit Code
	 * @param pageableData the Pageable Data
	 * @param filterByCriteriaData the Filtering Criteria
	 * @param criteria the Default Criteria
	 * @return {@link OrgDocumentListData} of type {@link B2BDocumentData}
	 */
	OrgDocumentListData<B2BDocumentData> getOrgDocumentList(final String b2bUnitCode, final PageableData pageableData,
			final FilterByCriteriaData filterByCriteriaData, final DefaultCriteria criteria);

	/**
	 * Returns a B2BDocumentModel based on supplied criteria
	 *
	 * @param b2bUnitCode the B2B Unit Code
	 * @param documentNumber the Document Number
	 * @return {@link B2BDocumentModel}
	 */
	B2BDocumentModel getDocumentByIdForUnit(String b2bUnitCode, String documentNumber);

	/**
	 * Returns B2B Document Media Attachment Data given the documentMediaModel
	 *
	 * @param documentMediaModel the Document Media Model
	 * @return {@link AttachmentData}
	 */
	AttachmentData getB2BDocumentAttachmentData(DocumentMediaModel documentMediaModel);
}
