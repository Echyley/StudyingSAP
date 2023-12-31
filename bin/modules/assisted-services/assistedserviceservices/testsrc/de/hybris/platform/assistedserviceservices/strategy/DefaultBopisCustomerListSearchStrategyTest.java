/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedserviceservices.strategy;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.customer.strategies.CustomerListSearchStrategy;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.site.BaseSiteService;

import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@IntegrationTest
public class DefaultBopisCustomerListSearchStrategyTest extends ServicelayerTransactionalTest
{
	private PageableData pageableData;

	@Resource
	private CustomerListSearchStrategy defaultBopisCustomerListSearchStrategy;

	@Resource
	private BaseSiteService baseSiteService;

	private BaseSiteModel baseSite;

	@Before
	public void setup() throws Exception
	{
		pageableData = new PageableData();
		pageableData.setPageSize(5);
		importCsv("/assistedserviceservices/test/instore_data.impex", "UTF-8");
		importCsv("/assistedserviceservices/test/pos_data.impex", "UTF-8");
		baseSite = baseSiteService.getBaseSiteForUID("testSite");
		baseSiteService.setCurrentBaseSite(baseSite, false);
		baseSite.setDataIsolationEnabled(Boolean.valueOf(false));
	}

	@Test
	public void bopisCustomerListSearchStrategyTestForNakano()
	{
		final SearchPageData<CustomerModel> customers = defaultBopisCustomerListSearchStrategy.getPagedCustomers("bopis",
				"customer.support@nakano.com", pageableData, null);

		assertEquals(2, customers.getResults().size());
		assertTrue(customers.getResults().stream().map(CustomerModel::getCustomerID).collect(Collectors.toList()).contains("user2@test.net"));
		assertTrue(customers.getResults().stream().map(CustomerModel::getCustomerID).collect(Collectors.toList()).contains("user1@test.net"));
	}

	@Test
	public void bopisCustomerListSearchStrategyTestForIchikawa()
	{
		final SearchPageData<CustomerModel> customers = defaultBopisCustomerListSearchStrategy.getPagedCustomers("bopis",
				"customer.support@ichikawa.com", pageableData, null);

		assertEquals(1, customers.getResults().size());
		assertEquals("user2@test.net", customers.getResults().get(0).getCustomerID());
	}

	@Test
	public void bopisCustomerListSearchStrategyTest()
	{
		final SearchPageData<CustomerModel> customers = defaultBopisCustomerListSearchStrategy.getPagedCustomers("bopis",
				"customer.support@general.com", pageableData, null);

		assertEquals(2, customers.getResults().size());
	}

	@Test
	public void bopisCustomerListSearchStrategyIsoLatedSiteTest()
	{
		baseSite.setDataIsolationEnabled(Boolean.valueOf(true));
		final SearchPageData<CustomerModel> customers = defaultBopisCustomerListSearchStrategy.getPagedCustomers("bopis",
				"customer.support@general.com", pageableData, null);

		assertEquals(0, customers.getResults().size());
	}

	@Test
	public void emptyBopisCustomerListSearchStrategyTest()
	{
		final SearchPageData<CustomerModel> customers = defaultBopisCustomerListSearchStrategy.getPagedCustomers("bopis",
				"asagent", pageableData, null);

		assertEquals(0, customers.getResults().size());
	}
}
