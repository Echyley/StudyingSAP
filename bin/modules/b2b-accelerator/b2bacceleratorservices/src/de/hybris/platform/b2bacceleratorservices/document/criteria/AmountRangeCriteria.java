/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.document.criteria;


import de.hybris.platform.b2bacceleratorservices.document.utils.AccountSummaryUtils;

import java.math.BigDecimal;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;


/**
 *
 */
public class AmountRangeCriteria extends RangeCriteria<BigDecimal>
{

	protected Optional<BigDecimal> startRange;
	protected Optional<BigDecimal> endRange;

	public AmountRangeCriteria(final String filterByKey)
	{
		this(filterByKey, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
	}

	public AmountRangeCriteria(final String filterByKey, final String startRange, final String endRange,
			final String documentStatus)
	{
		super(filterByKey, documentStatus);
		this.startRange = AccountSummaryUtils.parseBigDecimalToOptional(startRange);
		this.endRange = AccountSummaryUtils.parseBigDecimalToOptional(endRange);
	}

	/**
	 * @return the startRange
	 */
	@Override
	public Optional<BigDecimal> getStartRange()
	{
		return startRange;
	}

	/**
	 * @param startRange
	 *           the startRange to set
	 */
	@Override
	protected void setStartRange(final String startRange)
	{
		this.startRange = AccountSummaryUtils.parseBigDecimalToOptional(startRange);
	}

	/**
	 * @return the endRange
	 */
	@Override
	public Optional<BigDecimal> getEndRange()
	{
		return endRange;
	}

	/**
	 * @param endRange
	 *           the endRange to set
	 */
	@Override
	protected void setEndRange(final String endRange)
	{
		this.endRange = AccountSummaryUtils.parseBigDecimalToOptional(endRange);
	}
}
