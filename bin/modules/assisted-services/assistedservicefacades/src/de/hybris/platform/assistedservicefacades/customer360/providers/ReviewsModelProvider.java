/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.customer360.providers;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedservicefacades.customer360.ReviewData;
import de.hybris.platform.assistedservicefacades.customer360.ReviewDataList;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;


/**
 * Model provider implementation for Support Tickets fragment.
 */
public class ReviewsModelProvider implements FragmentModelProvider<ReviewDataList>
{
	private UserService userService;
	private CustomerReviewService customerReviewService;
	private SessionService sessionService;
	private SearchRestrictionService searchRestrictionService;

	private Converter<CustomerReviewModel, ReviewData> asmReviewConverter;

	@Override
	public ReviewDataList getModel(final Map<String, String> parameters)
	{
		final String listSize = parameters.get("listSize");

		if (StringUtils.isEmpty(listSize))
		{
			throw new IllegalArgumentException(
					"Fragment arguments are not provided for provider [" + ReviewsModelProvider.class.getName() + "] !");
		}
		int searchListSize;

		try
		{
			searchListSize = Integer.parseInt(listSize);
		}
		catch (final NumberFormatException formatException)
		{
			throw new IllegalArgumentException("Provided value [" + listSize + "] is not in a valid integer range!",
					formatException);
		}

		final CustomerModel user = (CustomerModel) getUserService().getCurrentUser();

		final List<CustomerReviewModel> reviewModels = getSessionService().executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				getSearchRestrictionService().disableSearchRestrictions();
				return getCustomerReviewService().getReviewsForCustomer(user);
			}
		});

		// Sort by Update Date desc as default sorting
		final ReviewDataList reviewDataList = new ReviewDataList();
		reviewDataList.setReviews(
				reviewModels.stream().sorted(Comparator.comparing(CustomerReviewModel::getModifiedtime).reversed())
						.limit(searchListSize).map(getAsmReviewConverter()::convert).collect(Collectors.toList())
		);
		return reviewDataList;
	}


	protected UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected CustomerReviewService getCustomerReviewService()
	{
		return customerReviewService;
	}

	public void setCustomerReviewService(final CustomerReviewService customerReviewService)
	{
		this.customerReviewService = customerReviewService;
	}

	protected SearchRestrictionService getSearchRestrictionService()
	{
		return searchRestrictionService;
	}

	public void setSearchRestrictionService(final SearchRestrictionService searchRestrictionService)
	{
		this.searchRestrictionService = searchRestrictionService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	public Converter<CustomerReviewModel, ReviewData> getAsmReviewConverter()
	{
		return asmReviewConverter;
	}

	public void setAsmReviewConverter(final Converter<CustomerReviewModel, ReviewData> asmReviewConverter)
	{
		this.asmReviewConverter = asmReviewConverter;
	}
}
