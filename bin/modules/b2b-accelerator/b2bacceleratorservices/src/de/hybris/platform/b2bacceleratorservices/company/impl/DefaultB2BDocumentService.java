/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.company.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateIfAnyResult;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2bacceleratorservices.dao.B2BDocumentDao;
import de.hybris.platform.b2bacceleratorservices.dao.PagedB2BDocumentDao;
import de.hybris.platform.b2bacceleratorservices.company.B2BDocumentService;
import de.hybris.platform.b2bacceleratorservices.document.AccountSummaryDocumentQuery;
import de.hybris.platform.b2bacceleratorservices.document.criteria.DefaultCriteria;
import de.hybris.platform.b2bacceleratorservices.enums.DocumentStatus;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;
import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.jalo.media.MediaManager;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Provides services for B2BDocument business logic/domain
 *
 */
public class DefaultB2BDocumentService implements B2BDocumentService
{
	private PagedB2BDocumentDao pagedB2BDocumentDao;
	private B2BDocumentDao b2bDocumentDao;
	private ModelService modelService;

	private static final Logger LOG = Logger.getLogger(DefaultB2BDocumentService.class.getName());


	@Override
	public SearchPageData<B2BDocumentModel> findDocuments(final AccountSummaryDocumentQuery query)
	{
		return getPagedB2BDocumentDao().findDocuments(query);
	}

	@Override
	public SearchResult<B2BDocumentModel> getOpenDocuments(final B2BUnitModel unit)
	{
		return getB2bDocumentDao().getOpenDocuments(unit);
	}

	@Override
	public SearchResult<B2BDocumentModel> getOpenDocuments(final MediaModel mediaModel)
	{
		return getB2bDocumentDao().getOpenDocuments(mediaModel);
	}

	@Required
	public void setPagedB2BDocumentDao(final PagedB2BDocumentDao pagedB2BDocumentDao)
	{
		this.pagedB2BDocumentDao = pagedB2BDocumentDao;
	}

	@Required
	public void setB2bDocumentDao(final B2BDocumentDao b2bDocumentDao)
	{
		this.b2bDocumentDao = b2bDocumentDao;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	public void deleteB2BDocumentFiles(final int numberOfDay, final List<B2BDocumentTypeModel> documentTypes,
			final List<DocumentStatus> documentStatuses)
	{
		final SearchResult<DocumentMediaModel> mediaList = getB2bDocumentDao().findOldDocumentMedia(numberOfDay, documentTypes,
				documentStatuses);

		for (final DocumentMediaModel media : mediaList.getResult())
		{
			MediaManager.getInstance().deleteMedia(media.getFolder().getQualifier(), media.getLocation());
			try
			{
				LOG.debug("[deleteB2BDocumentFiles] remove " + media.getLocation());
				getModelService().remove(media);
			}
			catch (final ModelRemovalException e)
			{
				LOG.error("[deleteB2BDocumentFiles]" + e);
			}
		}

	}


	@Override
	public SearchPageData<B2BDocumentModel> getPagedDocumentsForUnit(final String b2bUnitCode, final PageableData pageableData,
			final List<DefaultCriteria> criteriaList)
	{
		validateParameterNotNull(b2bUnitCode, "b2bUnitCode must not be null");
		validateParameterNotNull(pageableData, "pageableData must not be null");
		validateIfAnyResult(criteriaList, "criteria list must not be empty or null");
		return getPagedB2BDocumentDao().getPagedDocumentsForUnit(b2bUnitCode, pageableData, criteriaList);
	}

	/**
	 * Returns result for the search using unit code and document number
	 * @param b2bUnitCode
	 * @param documentNumber
	 * @return
	 */
	@Override
	public B2BDocumentModel getDocumentByIdForUnit(String b2bUnitCode, String documentNumber) {
		validateParameterNotNull(b2bUnitCode,"b2bUnitCode must not be null");
		validateParameterNotNull(documentNumber, "documentNumber must not be null");
		SearchResult<B2BDocumentModel> result = getB2bDocumentDao().findDocument(b2bUnitCode,documentNumber);
		if (result.getResult().isEmpty()) // searchData.resultList is never null, size = 0 when no documents are found
		{
			throw new ModelNotFoundException(
					"Document with id = %s not found in organizational unit %s.".formatted(documentNumber, b2bUnitCode));
		}

		return result.getResult().get(0);
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	protected PagedB2BDocumentDao getPagedB2BDocumentDao()
	{
		return pagedB2BDocumentDao;
	}


	protected B2BDocumentDao getB2bDocumentDao()
	{
		return b2bDocumentDao;
	}
}
