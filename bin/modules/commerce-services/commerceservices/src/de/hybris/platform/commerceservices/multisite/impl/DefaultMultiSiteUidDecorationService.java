/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.commerceservices.multisite.impl;

import de.hybris.platform.commerceservices.multisite.MultiSiteUidDecorationService;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;


/**
 * Default implementation for interface {@link MultiSiteUidDecorationService}
 * The Customer UID syntax is:
 * <p>
 * &lt Original Customer.uid &gt | &lt BaseSite.uid &gt
 * <p>
 * Assume BaseSite.uid is at the end of the string
 * </p>
 */
public class DefaultMultiSiteUidDecorationService implements MultiSiteUidDecorationService
{
	public static final int UID_DECORATION_ELEMENT_LENGTH = 2;

	private static final String UID_DECORATION_DELIMITER = "|";
	private static final String PARAMETER_CUSTOMER_UID_CAN_NOT_BE_NULL = "Parameter [customerUid] can not be null";
	private static final String PARAMETER_SITE_UID_CAN_NOT_BE_NULL = "Parameter [siteUid] can not be null";
	private static final String PARAMETER_PLAIN_CUSTOMER_UID_CAN_NOT_BE_NULL = "Parameter [plainCustomerUid] can not be null";

	@Override
	public boolean isDecorated(final String customerUid, final String siteUid)
	{
		validateParameterNotNull(customerUid, PARAMETER_CUSTOMER_UID_CAN_NOT_BE_NULL);
		validateParameterNotNull(siteUid, PARAMETER_SITE_UID_CAN_NOT_BE_NULL);

		final Optional<String> lastPart = getLastPart(customerUid);

		return lastPart.filter(s -> Objects.equals(siteUid, s)).isPresent();

	}

	@Override
	public String decorate(final String plainCustomerUid, final String siteUid)
	{
		validateParameterNotNull(plainCustomerUid, PARAMETER_PLAIN_CUSTOMER_UID_CAN_NOT_BE_NULL);
		validateParameterNotNull(siteUid, PARAMETER_SITE_UID_CAN_NOT_BE_NULL);

		return plainCustomerUid + UID_DECORATION_DELIMITER + siteUid;
	}

	@Override
	public String[] undecorate(final String customerUid, final String siteUid)
	{
		validateParameterNotNull(customerUid, PARAMETER_CUSTOMER_UID_CAN_NOT_BE_NULL);
		validateParameterNotNull(siteUid, PARAMETER_SITE_UID_CAN_NOT_BE_NULL);

		final String[] elements;

		final Optional<String> lastPart = getLastPart(customerUid);
		if (lastPart.isPresent() && Objects.equals(siteUid, lastPart.get()))
		{
			elements = new String[UID_DECORATION_ELEMENT_LENGTH];
			elements[0] = customerUid.substring(0, customerUid.lastIndexOf(UID_DECORATION_DELIMITER));
			elements[1] = siteUid;
		}
		else
		{
			elements = new String[1];
			elements[0] = customerUid;
		}

		return elements;
	}

	private Optional<String> getLastPart(final String customerUid)
	{
		final int index = customerUid.lastIndexOf(UID_DECORATION_DELIMITER);
		if (index != StringUtils.INDEX_NOT_FOUND)
		{
			final String lastPart = customerUid.substring(index + 1);
			return Optional.of(lastPart);
		}

		return Optional.empty();
	}
}
