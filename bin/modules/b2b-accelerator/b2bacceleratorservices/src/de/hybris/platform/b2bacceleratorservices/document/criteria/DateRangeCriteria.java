/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.document.criteria;


import de.hybris.platform.b2bacceleratorservices.document.utils.AccountSummaryUtils;

import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;


/**
 *
 */
public class DateRangeCriteria extends RangeCriteria<Date>
{

	protected Optional<Date> startRange;
	protected Optional<Date> endRange;

	public DateRangeCriteria(final String filterByKey)
	{
		this(filterByKey, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
	}

	public DateRangeCriteria(final String filterByKey, final String startRange, final String endRange, final String documentStatus)
	{
		super(filterByKey, documentStatus);
		this.startRange = AccountSummaryUtils.parseDateToOptional(startRange);
		this.endRange = AccountSummaryUtils.parseDateToOptional(endRange);
	}

	/**
	 * @return the startRange
	 */
	@Override
	public Optional<Date> getStartRange()
	{
		return this.startRange;
	}

	/**
	 * @param startRange
	 *           the startRange to set
	 */
	@Override
	protected void setStartRange(final String startRange)
	{
		this.startRange = AccountSummaryUtils.parseDateToOptional(startRange);
	}

	/**
	 * @return the endRange
	 */
	@Override
	public Optional<Date> getEndRange()
	{
		return this.endRange;
	}

	/**
	 * @param endRange
	 *           the endRange to set
	 */
	@Override
	protected void setEndRange(final String endRange)
	{
		this.endRange = AccountSummaryUtils.parseDateToOptional(endRange);
	}
}
