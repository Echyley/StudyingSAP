/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.hook.impl;

import de.hybris.platform.commerceservices.order.hook.CartEarliestRetrievalDateHook;
import de.hybris.platform.core.model.order.CartModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;



/**
 * Default implementation for {@link CartEarliestRetrievalDateHook}
 */
public class DefaultCartEarliestRetrievalDateHook implements CartEarliestRetrievalDateHook
{

	private static final String DATE_FORMAT = "yyyy-MM-dd";

	@Override
	public List<String> getEarliestRetrievalDates(final CartModel cart)
	{
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		final String earliestRetrievalDate = LocalDate.now().format(formatter);
		return Collections.singletonList(earliestRetrievalDate);
	}

}
