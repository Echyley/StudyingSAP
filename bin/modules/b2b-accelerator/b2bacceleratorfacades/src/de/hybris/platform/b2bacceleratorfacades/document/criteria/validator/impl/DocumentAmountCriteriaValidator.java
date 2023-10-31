/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorfacades.document.criteria.validator.impl;


import de.hybris.platform.b2bacceleratorfacades.document.criteria.validator.DocumentCriteriaValidator;
import de.hybris.platform.b2bacceleratorservices.document.utils.AccountSummaryUtils;

import java.math.BigDecimal;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 */
public class DocumentAmountCriteriaValidator implements DocumentCriteriaValidator
{
	private static final Logger LOG = Logger.getLogger(DocumentAmountCriteriaValidator.class);

	@Override
	public boolean isValid(final String startRange, final String endRange)
	{

		Optional<BigDecimal> parsedStartRange = Optional.empty();
		Optional<BigDecimal> parsedEndRange = Optional.empty();

		if (LOG.isDebugEnabled())
		{
			LOG.debug("validating amount ranges");
		}

		if (StringUtils.isNotBlank(startRange))
		{
			parsedStartRange = AccountSummaryUtils.parseBigDecimal(startRange);
			if (!parsedStartRange.isPresent())
			{
				return false;
			}
		}

		if (StringUtils.isNotBlank(endRange))
		{
			parsedEndRange = AccountSummaryUtils.parseBigDecimal(endRange);
			if (!parsedEndRange.isPresent())
			{
				return false;
			}
		}

		return !(parsedStartRange.isPresent() && parsedEndRange.isPresent()
				&& parsedStartRange.get().compareTo(parsedEndRange.get()) > 0);

	}
}
