/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.order.strategies.impl;

import de.hybris.platform.commerceservices.order.strategies.CartRetrievalDateStrategy;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Default implementation for {@link CartRetrievalDateStrategy}
 */
public class DefaultCartRetrievalDateStrategy implements CartRetrievalDateStrategy
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultCartRetrievalDateStrategy.class);

	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private ModelService modelService;

	@Override
	public void updateRequestedRetrievalDate(final CartModel cart, final String requestedRetrievalDate)
	{
		cart.setRequestedRetrievalDate(requestedRetrievalDate);
		getModelService().save(cart);
	}

	@Override
	public String getCartEarliestRetrievalDate(final List<String> retrievalDates)
	{
		if (retrievalDates == null || retrievalDates.isEmpty())
		{
			return null;
		}
		final DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

		final List<Date> retrievalDateList = convertDates(retrievalDates, formatter);

		final Optional<Date> earliestRetrievalDate = retrievalDateList.stream().max(Date::compareTo);

		return earliestRetrievalDate.isPresent() ? formatter.format(earliestRetrievalDate.get()) : null;
	}

	protected List<Date> convertDates(final List<String> retrievalDates, final DateFormat formatter)
	{
		final List<Date> retrievalDateList = new ArrayList<>();

		retrievalDates.forEach(retrievalDate -> {
			try
			{
				final Date date = formatter.parse(retrievalDate);
				retrievalDateList.add(date);
			}
			catch (final ParseException e)
			{
				LOG.debug("Date is not in expected format {} : {} ", DATE_FORMAT, retrievalDate);
			}
		});
		return retrievalDateList;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
