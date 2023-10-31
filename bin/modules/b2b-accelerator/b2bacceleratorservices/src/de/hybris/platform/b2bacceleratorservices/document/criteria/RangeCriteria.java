/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.document.criteria;


import de.hybris.platform.b2bacceleratorservices.document.utils.AccountSummaryUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 *
 */
public class RangeCriteria<T> extends DefaultCriteria
{
	private static final Logger LOG = Logger.getLogger(RangeCriteria.class);

	protected Optional<T> startRange;
	protected Optional<T> endRange;

	public RangeCriteria(final String filterByKey)
	{
		this(filterByKey, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
	}

	public RangeCriteria(final String filterByKey, final String documentStatus)
	{
		super(filterByKey, documentStatus);
	}

	public RangeCriteria(final String filterByKey, final String startRange, final String endRange, final String documentStatus)
	{
		super(filterByKey, documentStatus);
		this.startRange = AccountSummaryUtils.toOptional(startRange);
		this.endRange = AccountSummaryUtils.toOptional(endRange);
	}

	/**
	 * @return the startRange
	 */
	public Optional<T> getStartRange()
	{
		return this.startRange;
	}

	/**
	 * @param startRange
	 *           the startRange to set
	 */
	protected void setStartRange(final String startRange)
	{
		this.startRange = AccountSummaryUtils.toOptional(startRange);
	}

	/**
	 * @return the endRange
	 */
	public Optional<T> getEndRange()
	{
		return this.endRange;
	}

	/**
	 * @param endRange
	 *           the endRange to set
	 */
	protected void setEndRange(final String endRange)
	{
		this.endRange = AccountSummaryUtils.toOptional(endRange);
	}

	@Override
	public void setCriteriaValues(final FilterByCriteriaData filterByCriteriaData)
	{
		super.setCriteriaValues(filterByCriteriaData);
		this.setStartRange(filterByCriteriaData.getStartRange());
		this.setEndRange(filterByCriteriaData.getEndRange());
	}

	@Override
	public void populateCriteriaQueryAndParamsMap(final List<String> whereQueryList, final Map<String, Object> queryParamsMap)
	{

		if (this.getStartRange().isPresent())
		{
			final String formattedQuery = String.format(RANGE_QUERY, getFilterByKey(), ">", "?startRange");
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format(QUERY_CRITERIA, formattedQuery, this.getStartRange().get()));
			}
			whereQueryList.add(formattedQuery);
			queryParamsMap.put("startRange", this.getStartRange().get());
		}

		if (this.getEndRange().isPresent())
		{
			final String formattedQuery = String.format(RANGE_QUERY, getFilterByKey(), "<", "?endRange");
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format(QUERY_CRITERIA, formattedQuery, this.getEndRange().get()));
			}
			whereQueryList.add(formattedQuery);
			queryParamsMap.put("endRange", this.getEndRange().get());
		}

		super.populateCriteriaQueryAndParamsMap(whereQueryList, queryParamsMap);
	}
}
