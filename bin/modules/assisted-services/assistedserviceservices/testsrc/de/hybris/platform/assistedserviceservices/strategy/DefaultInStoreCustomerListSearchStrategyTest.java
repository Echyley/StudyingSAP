/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedserviceservices.strategy;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.customer.strategies.CustomerListSearchStrategy;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.site.BaseSiteService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultInStoreCustomerListSearchStrategyTest extends ServicelayerTransactionalTest
{
	private PageableData pageableData;

	@Resource
	private CustomerListSearchStrategy defaultInStoreCustomerListSearchStrategy;

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

	@Test(expected = IllegalArgumentException.class)
	public void instoreCuystomerListSearchStrategyEmptyParamsTest()
	{
		defaultInStoreCustomerListSearchStrategy.getPagedCustomers(null, null, null, null);
	}


	@Test(expected = UnknownIdentifierException.class)
	public void instoreCuystomerListSearchStrategyInvalidEmployeeTest()
	{
		defaultInStoreCustomerListSearchStrategy.getPagedCustomers("InStore", "INVALID_EMPLOYEE", pageableData, null);
	}

	@Test
	public void instoreCuystomerListSearchStrategyNakanoTest()
	{
		final SearchPageData<CustomerModel> customers = defaultInStoreCustomerListSearchStrategy.getPagedCustomers("InStore",
				"customer.support@nakano.com", pageableData, null);

		assertEquals(1, customers.getResults().size());
	}

	@Test
	public void instoreCuystomerListSearchStrategyIchikawaTestForIsolatedSite()
	{
		baseSite.setDataIsolationEnabled(Boolean.valueOf(true));
		final SearchPageData<CustomerModel> customers = defaultInStoreCustomerListSearchStrategy.getPagedCustomers("InStore",
				"customer.support@ichikawa.com", pageableData, null);

		assertEquals(0, customers.getResults().size());
	}

	@Test
	public void instoreCuystomerListSearchStrategyIchikawaTest()
	{
		final SearchPageData<CustomerModel> customers = defaultInStoreCustomerListSearchStrategy.getPagedCustomers("InStore",
				"customer.support@ichikawa.com", pageableData, null);

		assertEquals(2, customers.getResults().size());
	}

	@Test
	public void instoreCuystomerListSearchStrategyNakanoAndIchikawaTest()
	{
		final SearchPageData<CustomerModel> customers = defaultInStoreCustomerListSearchStrategy.getPagedCustomers("InStore",
				"customer.support@manager.com", pageableData, null);

		assertEquals(3, customers.getResults().size());
	}
}
