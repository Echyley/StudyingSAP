/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedserviceservices.strategy;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.assistedserviceservices.events.CustomerSupportEventService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.ticket.enums.EventType;
import de.hybris.platform.ticketsystem.events.SessionEvent;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


@IntegrationTest
public class DefaultRecentlyStartedSessionCustomerListSearchStrategyTest extends ServicelayerTransactionalTest
{
	private PageableData pageableData;

	@Resource
	private UserService userService;

	@Resource
	private CustomerSupportEventService customerSupportEventService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private DefaultRecentlyStartedSessionCustomerListSearchStrategy defaultRecentlyStartedSessionCustomerListSearchStrategy;

	@Before
	public void setup() throws Exception
	{
		pageableData = new PageableData();
		pageableData.setPageSize(5);
		importCsv("/assistedserviceservices/test/recent_data.impex", "UTF-8");
		BaseSiteModel isolatedSite = baseSiteService.getBaseSiteForUID("isolatedSite");
		isolatedSite.setDataIsolationEnabled(Boolean.TRUE);
		BaseSiteModel commonSite = baseSiteService.getBaseSiteForUID("testSite");
		commonSite.setDataIsolationEnabled(Boolean.FALSE);
		baseSiteService.setCurrentBaseSite((BaseSiteModel)null, false);
	}

	@Test
	public void recentCustomerListSearchStrategyTest()
	{
		EmployeeModel asagent = userService.getUserForUID("asagent", EmployeeModel.class);
		CustomerModel user1 = userService.getUserForUID("user1", CustomerModel.class);
		CustomerModel user2 = userService.getUserForUID("user2", CustomerModel.class);
		CustomerModel user3 = userService.getUserForUID("user3", CustomerModel.class);
		CustomerModel user4 = userService.getUserForUID("user4", CustomerModel.class);

		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user1, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user2, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user3, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user4, EventType.START_SESSION_EVENT));

		SearchPageData<CustomerModel> customers = defaultRecentlyStartedSessionCustomerListSearchStrategy.getPagedCustomers("recent",
				"asagent", pageableData, null);

		assertEquals(2, customers.getResults().size());

		// test with query
		final Map queryMap = new HashMap<String, String>();
		queryMap.put("query", "user1");
		customers = defaultRecentlyStartedSessionCustomerListSearchStrategy.getPagedCustomers("recent",
				"asagent", pageableData, queryMap);
		assertEquals(1, customers.getResults().size());
		assertEquals("user1", customers.getResults().get(0).getUid());

		// test with query 2, new ignore case
		queryMap.clear();;
		queryMap.put("query", "UsEr1");
		customers = defaultRecentlyStartedSessionCustomerListSearchStrategy.getPagedCustomers("recent",
				"asagent", pageableData, queryMap);
		assertEquals(1, customers.getResults().size());
		assertEquals("user1", customers.getResults().get(0).getUid());
	}

	@Test
	public void recentCustomerListSearchStrategyTestIsolated()
	{
		// create sites
		BaseSiteModel isolatedSite = baseSiteService.getBaseSiteForUID("isolatedSite");
		BaseSiteModel isolatedSite2 = baseSiteService.getBaseSiteForUID("isolatedSite2");

		EmployeeModel asagent = userService.getUserForUID("asagent", EmployeeModel.class);
		CustomerModel user1 = userService.getUserForUID("newuser1", CustomerModel.class);
		CustomerModel user2 = userService.getUserForUID("newuser2|isolatedSite", CustomerModel.class);
		CustomerModel user3 = userService.getUserForUID("newuser3|isolatedSite", CustomerModel.class);
		CustomerModel user4 = userService.getUserForUID("newuser4|isolatedSite2", CustomerModel.class);

		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user1, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user2, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user3, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user4, EventType.START_SESSION_EVENT));

		// search with isolated site 1, 2 customers belongs to here
		baseSiteService.setCurrentBaseSite(isolatedSite, false);
		final SearchPageData<CustomerModel> customersForSite1 = defaultRecentlyStartedSessionCustomerListSearchStrategy.getPagedCustomers("recent",
				"asagent", pageableData, null);

		assertEquals(2, customersForSite1.getResults().size());

		// search with isolated site 2, 1 customer belongs to here
		baseSiteService.setCurrentBaseSite(isolatedSite2, false);
		final SearchPageData<CustomerModel> customersForSite2 = defaultRecentlyStartedSessionCustomerListSearchStrategy.getPagedCustomers("recent",
				"asagent", pageableData, null);

		assertEquals(1, customersForSite2.getResults().size());
		assertEquals("newuser4", customersForSite2.getResults().get(0).getUndecoratedUid());

	}

	@Test
	public void recentCustomerListSearchStrategyTestCommonSite()
	{
		// create site
		BaseSiteModel commonSite = baseSiteService.getBaseSiteForUID("testSite");

		EmployeeModel asagent = userService.getUserForUID("asagent", EmployeeModel.class);
		CustomerModel user1 = userService.getUserForUID("newuser1", CustomerModel.class);
		CustomerModel user2 = userService.getUserForUID("newuser2|isolatedSite", CustomerModel.class);
		CustomerModel user3 = userService.getUserForUID("newuser3|isolatedSite", CustomerModel.class);

		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user1, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user2, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user3, EventType.START_SESSION_EVENT));

		// search with common site 1
		baseSiteService.setCurrentBaseSite(commonSite, false);
		final SearchPageData<CustomerModel> customers = defaultRecentlyStartedSessionCustomerListSearchStrategy.getPagedCustomers(
				"recent", "asagent", pageableData, null);

		assertEquals(1, customers.getResults().size());
		assertEquals("newuser1", customers.getResults().get(0).getUndecoratedUid());
	}
	@Test
	public void recentCustomerListSearchStrategyTestWithExceedPage()
	{
		EmployeeModel asagent = userService.getUserForUID("asagent", EmployeeModel.class);
		CustomerModel user2 = userService.getUserForUID("user2", CustomerModel.class);
		CustomerModel user1 = userService.getUserForUID("user1", CustomerModel.class);
		CustomerModel user3 = userService.getUserForUID("user3", CustomerModel.class);
		CustomerModel user4 = userService.getUserForUID("user4", CustomerModel.class);
		CustomerModel user5 = userService.getUserForUID("user5", CustomerModel.class);
		CustomerModel user6 = userService.getUserForUID("user6", CustomerModel.class);
		CustomerModel user7 = userService.getUserForUID("user7", CustomerModel.class);

		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user1, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user1, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user1, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user2, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user2, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user3, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user4, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user5, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user6, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user7, EventType.START_SESSION_EVENT));
		customerSupportEventService.registerSessionEvent(createSessionEvent(asagent, user7, EventType.START_SESSION_EVENT));

		final SearchPageData<CustomerModel> customers = defaultRecentlyStartedSessionCustomerListSearchStrategy.getPagedCustomers("recent",
				"asagent", pageableData, null);

		assertEquals(5, customers.getResults().size());
	}

	@Test
	public void emptyRecentCustomerListSearchStrategyTest()
	{
		final SearchPageData<CustomerModel> customers = defaultRecentlyStartedSessionCustomerListSearchStrategy.getPagedCustomers("recent",
				"asagent", pageableData, null);

		assertEquals(0, customers.getResults().size());
	}

	protected SessionEvent createSessionEvent(UserModel agent, UserModel customer, EventType type)
	{
		final SessionEvent asmStartSessionEventData = new SessionEvent();
		asmStartSessionEventData.setAgent(agent);
		asmStartSessionEventData.setCustomer(customer);
		asmStartSessionEventData.setCreatedAt(new Date());
		asmStartSessionEventData.setAgentGroups(new ArrayList<>(agent.getGroups()));
		asmStartSessionEventData.setEventType(type);
		return asmStartSessionEventData;
	}
}
