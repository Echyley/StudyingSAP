/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorfacades.document.criteria.validator.impl;

import de.hybris.platform.b2bacceleratorservices.constants.B2BAccountSummaryConstants;
import de.hybris.platform.b2bacceleratorfacades.document.criteria.validator.DocumentCriteriaValidator;
import de.hybris.platform.b2bacceleratorservices.document.utils.AccountSummaryUtils;

import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 *
 */
public class DocumentDateCriteriaValidator implements DocumentCriteriaValidator
{
	private static final Logger LOG = Logger.getLogger(DocumentDateCriteriaValidator.class);

	@Override
	public boolean isValid(final String startRange, final String endRange)
	{

		Optional<Date> parsedStartRange = Optional.empty();
		Optional<Date> parsedEndRange = Optional.empty();

		if (LOG.isDebugEnabled())
		{
			LOG.debug("validating date ranges");
		}

		if (StringUtils.isNotBlank(startRange))
		{
			parsedStartRange = AccountSummaryUtils.parseDate(startRange, B2BAccountSummaryConstants.DATE_FORMAT_MM_DD_YYYY);
			if (!parsedStartRange.isPresent())
			{
				return false;
			}

		}

		if (StringUtils.isNotBlank(endRange))
		{
			parsedEndRange = AccountSummaryUtils.parseDate(endRange, B2BAccountSummaryConstants.DATE_FORMAT_MM_DD_YYYY);
			if (!parsedEndRange.isPresent())
			{
				return false;
			}
		}

		return !(parsedStartRange.isPresent() && parsedEndRange.isPresent() && parsedStartRange.get().after(parsedEndRange.get()));
	}
}
