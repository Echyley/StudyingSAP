/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedserviceservices.events;


import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.ticket.enums.EventType;
import de.hybris.platform.ticketsystem.events.SessionEvent;
import de.hybris.platform.ticketsystem.events.model.SessionEventModel;

import java.util.Date;
import java.util.Map;

/**
 * Customer Support events service.
 */
public interface CustomerSupportEventService
{
	/**
	 * creates session event
	 *
	 * @param asmEventData the event data
	 */
	void registerSessionEvent(SessionEvent asmEventData);

	/**
	 * Search for agent-specific events
	 *
	 * @param agent AS agent
	 * @param eventType type
	 * @param startDate start date
	 * @param endDate end date
	 * @param pageableData pageable data
	 * @param limit limit of returned customers
	 *
	 * @return SearchPageData<CsSessionEventModel>
	 * @deprecated since 6.7
 */
	@Deprecated(since = "6.7", forRemoval = true)
	SearchPageData<SessionEventModel> getAllEventsForAgent(EmployeeModel agent, EventType eventType, Date startDate,
			Date endDate, PageableData pageableData, int limit);

	/**
	 * Searching customers based on event type
	 *
	 * @param agent
	 *           - can be empty or null
	 *
	 * @param eventType
	 *           event type to search for
 	 * @param startDate
 	 *           start date
	 * @param endDate
	 *           end date
	 * @param pageableData
	 *           paging and sorting information
	 * @param limit
	 *           limit of cs session events returned
	 * @param includeDisabledAccounts
	 * 			include disabled customers or no
	 * @return search page data for CS Customer Model
	 */
	<T extends CustomerModel> SearchPageData<T> findAllCustomersByEventsAndAgent(final EmployeeModel agent, final EventType eventType,
		 final Date startDate, final Date endDate, final PageableData pageableData, final int limit, final boolean includeDisabledAccounts);
	/**
	 * Searching customers based on event type
	 *
	 * @param agent
	 *           - can be empty or null
	 *
	 * @param eventType
	 *           event type to search for
	 * @param startDate
	 *           start date
	 * @param endDate
	 *           end date
	 * @param pageableData
	 *           paging and sorting information
	 * @param limit
	 *           limit of cs session events returned
	 * @param includeDisabledAccounts
	 *           include disabled customers or no
	 * @param parameterMap
	 *           extra parameters
	 * @return search page data for CS Customer Model
	 */
	default <T extends CustomerModel> SearchPageData<T> findAllCustomersByEventsAndAgent(final EmployeeModel agent, final EventType eventType,
			final Date startDate, final Date endDate, final PageableData pageableData, final int limit, final boolean includeDisabledAccounts, Map<String, Object> parameterMap)
	{
		throw new UnsupportedOperationException();
	}
	/**
	 * Search for customer based on event parameters.
	 *
	 * @param agent AS agent
	 * @param eventType type
	 * @param startDate start date
	 * @param endDate end date
	 * @param pageableData pageable data
	 * @param limit limit of returned customers
	 * @param <T>
	 * @return CustomerModel based on event parameters
	 */
	<T extends CustomerModel> SearchPageData<T> findAllCustomersByEventsAndAgent(final EmployeeModel agent,
		final EventType eventType, final Date startDate, final Date endDate, final PageableData pageableData, final int limit);
}
