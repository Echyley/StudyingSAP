/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.dao.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.collections4.CollectionUtils;

import de.hybris.platform.b2bacceleratorservices.dao.B2BDocumentDao;
import de.hybris.platform.b2bacceleratorservices.enums.DocumentStatus;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentModel;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;
import de.hybris.platform.b2bacceleratorservices.model.DocumentMediaModel;


public class DefaultB2BDocumentDao extends DefaultGenericDao<B2BDocumentModel> implements B2BDocumentDao
{

	private static final String DOCUMENT_MEDIA_PK = "documentMediaPk";

	private static final String FIND_DOCUMENT = "SELECT {" + B2BDocumentModel._TYPECODE + ":pk}  FROM { "
			+ B2BDocumentModel._TYPECODE + " as " + B2BDocumentModel._TYPECODE + " join " + B2BUnitModel._TYPECODE + " as "
			+ B2BUnitModel._TYPECODE + " on { " + B2BDocumentModel._TYPECODE + ":unit } = {" + B2BUnitModel._TYPECODE + ":pk}} "
			+ " where {" + B2BUnitModel._TYPECODE + ":uid} = ?unit and {" + B2BDocumentModel._TYPECODE + ":status} = ?status ";

	private static final String FIND_DOCUMENT_BY_ID = "SELECT {" + B2BDocumentModel._TYPECODE + ":pk}  FROM { "
			+ B2BDocumentModel._TYPECODE + " as " + B2BDocumentModel._TYPECODE + " join " + B2BUnitModel._TYPECODE + " as "
			+ B2BUnitModel._TYPECODE + " on { " + B2BDocumentModel._TYPECODE + ":unit } = {" + B2BUnitModel._TYPECODE + ":pk}} "
			+ " where {" + B2BUnitModel._TYPECODE + ":uid} = ?unit and {" + B2BDocumentModel._TYPECODE + ":status} = ?status " +
			"and {"+ B2BDocumentModel._TYPECODE+ ":documentNumber}=?documentNumber ";

	private static final String FIND_DOCUMENT_BY_DOCUMENT_MEDIA = "SELECT {" + B2BDocumentModel._TYPECODE + ":pk}  FROM { "
			+ B2BDocumentModel._TYPECODE + " as " + B2BDocumentModel._TYPECODE + "} where {" + B2BDocumentModel._TYPECODE
			+ ":documentMedia} = ?documentMediaPk ";

	private static final String FIND_DOCUMENT_IGNORE_UNIT = "SELECT {" + DocumentMediaModel._TYPECODE + ":pk}  " + "FROM { "
			+ B2BDocumentModel._TYPECODE + " as " + B2BDocumentModel._TYPECODE + " join " + B2BDocumentTypeModel._TYPECODE + " as "
			+ B2BDocumentTypeModel._TYPECODE + " on {" + B2BDocumentModel._TYPECODE + ":documentType} = {"
			+ B2BDocumentTypeModel._TYPECODE + ":pk} " + "join " + DocumentMediaModel._TYPECODE + " as "
			+ DocumentMediaModel._TYPECODE + " on {" + B2BDocumentModel._TYPECODE + ":documentMedia} = {"
			+ DocumentMediaModel._TYPECODE + ":pk} " + "} ";

	private static final String PARAMETER_CREATION_TIME = "creationtime";
	private static final String PARAMETER_DOCUMENT_TYPES = "documenttypes";
	private static final String PARAMETER_DOCUMENT_STATUSES = "documentstatuses";

	public DefaultB2BDocumentDao()
	{
		super(B2BDocumentModel._TYPECODE);
	}

	@Override
	public SearchResult<B2BDocumentModel> getOpenDocuments(final B2BUnitModel unit)
	{

		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_DOCUMENT);
		query.addQueryParameter(B2BDocumentModel.UNIT, unit.getUid());
		query.addQueryParameter(B2BDocumentModel.STATUS, DocumentStatus.OPEN);
		return getFlexibleSearchService().search(query);
	}

	@Override
	public SearchResult<B2BDocumentModel> getOpenDocuments(final MediaModel mediaModel)
	{

		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_DOCUMENT_BY_DOCUMENT_MEDIA);
		query.addQueryParameter(DOCUMENT_MEDIA_PK, mediaModel.getPk());
		return getFlexibleSearchService().search(query);
	}

	@Override
	public SearchResult<DocumentMediaModel> findOldDocumentMedia(final int numberOfDays,
			final List<B2BDocumentTypeModel> documentTypes, final List<DocumentStatus> documentStatuses)
	{
		final StringBuilder whereStatement = new StringBuilder();
		whereStatement.append(" where ");

		final Map<String, Object> queryParams = new HashMap<>();

		//add criteria for creation date of b2b document
		final Date earliestFileDate = getEarliestFileDate(numberOfDays);
		whereStatement.append("{" + B2BDocumentModel._TYPECODE + ":" + B2BDocumentModel.CREATIONTIME + "} < ?"
				+ PARAMETER_CREATION_TIME);
		queryParams.put(PARAMETER_CREATION_TIME, earliestFileDate);

		//add criteria for document type
		if (!CollectionUtils.isEmpty(documentTypes))
		{
			whereStatement.append(" and {" + B2BDocumentTypeModel._TYPECODE + ":" + B2BDocumentTypeModel.PK + "} in ( ?"
					+ PARAMETER_DOCUMENT_TYPES + ")");
			queryParams.put(PARAMETER_DOCUMENT_TYPES, documentTypes);
		}

		//add criteria for document status
		if (!CollectionUtils.isEmpty(documentStatuses))
		{
			whereStatement.append(" and {" + B2BDocumentModel._TYPECODE + ":" + B2BDocumentModel.STATUS + "} in ( ?"
					+ PARAMETER_DOCUMENT_STATUSES + ")");
			queryParams.put(PARAMETER_DOCUMENT_STATUSES, documentStatuses);
		}

		//search
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_DOCUMENT_IGNORE_UNIT + whereStatement.toString());
		query.addQueryParameters(queryParams);



		return getFlexibleSearchService().search(query);

	}

	@Override
	public SearchResult<B2BDocumentModel> findDocument(String b2bUnitCode, String documentNumber) {

		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_DOCUMENT_BY_ID);
		query.addQueryParameter(B2BDocumentModel.UNIT, b2bUnitCode);
		query.addQueryParameter(B2BDocumentModel.STATUS, DocumentStatus.OPEN);
		query.addQueryParameter(B2BDocumentModel.DOCUMENTNUMBER,documentNumber);
		return getFlexibleSearchService().search(query);
	}

	/**
     * Calculates the earliest file date as per the file age
     * for example: today 2000-01-5, numberOfDay is 4, so earliest file date is 2000-01-01
	 */
	protected Date getEarliestFileDate(final int numberOfDay)
	{

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1 * numberOfDay);
		c = DateUtils.truncate(c, Calendar.DATE);

		return c.getTime();
	}

}
