/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.hybris.statistics.collector.commerceservices;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.context.ApplicationContext;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.hybris.statistics.collector.BusinessStatisticsCollector;

/**
 * This class will be used to collect
 * how many sites of a cloud environment have data isolation enabled?
 * The return will be like  Map.of("sites", Map.of("numIsolatedSites", 1234));
 */
public class SitesStatisticsCollector implements BusinessStatisticsCollector<Map<String, Map<String, Object>>>
{
	private final FlexibleSearchService flexibleSearchService;
	private final SessionService sessionService;
	private final UserService userService;

	public SitesStatisticsCollector()
	{
		super();
		final ApplicationContext ctx = Registry.getApplicationContext();
		flexibleSearchService = ctx.getBean("flexibleSearchService", FlexibleSearchService.class);
		sessionService = ctx.getBean("sessionService", SessionService.class);
		userService = ctx.getBean("userService", UserService.class);
	}

	@Override
	public Map<String, Map<String, Object>> collectStatistics()
	{
		final Map<String, Object> result = new HashMap<>();
		result.put("numIsolatedSites", getNumSites());
		return ImmutableMap.<String, Map<String, Object>>builder().put("sites", result).build();
	}

	private Integer getNumSites()
	{
		return sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public Object execute()
			{
				userService.setCurrentUser(userService.getAdminUser());
				final FlexibleSearchQuery fsQuery = new FlexibleSearchQuery(getSiteCountQuery());

				fsQuery.setResultClassList(Lists.<Class>newArrayList(Integer.class));

				final SearchResult<Integer> result = flexibleSearchService.search(fsQuery);
				final Iterator<Integer> iterator = result.getResult().iterator();
				return iterator.hasNext() ? iterator.next() : Integer.valueOf(0);
			}

			private String getSiteCountQuery()
			{
				final StringBuilder builder = new StringBuilder("SELECT count({");
				builder.append(BaseSiteModel.PK).append("}) FROM {");
				builder.append(BaseSiteModel._TYPECODE).append("}");
				builder.append("WHERE {").append(BaseSiteModel.DATAISOLATIONENABLED).append("} = true");

				return builder.toString();
			}
		});
	}

}
