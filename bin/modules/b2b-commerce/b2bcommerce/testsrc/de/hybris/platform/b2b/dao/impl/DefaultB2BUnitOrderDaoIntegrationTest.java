/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.b2b.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import javax.annotation.Resource;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static de.hybris.platform.b2b.util.B2BCommerceTestUtils.createPageableData;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;


/**
 * Integration test suite for {@link DefaultB2BUnitOrderDaoIntegrationTest}
 */
@IntegrationTest
public class DefaultB2BUnitOrderDaoIntegrationTest extends ServicelayerTest
{
	@Resource
	private DefaultB2BUnitOrderDao defaultB2BUnitOrderDao;
	@Resource
	private UserService userService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private BaseStoreService baseStoreService;
	@Resource
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	@Resource
	private B2BCustomerService<B2BCustomerModel, B2BUnitModel> b2bCustomerService;
	private static final String TEST_BASESITE_UID = "testSite";
	private static final String UNIT_VIEWER_CUSTOMER1_UID = "customer1";
	private static final String UNIT_FILTER_PARAM = "unit";
	private static final String BUYER_FILTER_PARAM = "user";
	private static final PageableData pageableData = createPageableData();

	@Before
	public void setUp() throws Exception
	{
		userService.setCurrentUser(userService.getAdminUser());
		importCsv("/b2bcommerce/test/testDefaultB2BUnitOrder.impex", "utf-8");

		BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(TEST_BASESITE_UID);
		baseSiteService.setCurrentBaseSite(baseSite, false);

		UserModel user = userService.getUserForUID(UNIT_VIEWER_CUSTOMER1_UID);
		userService.setCurrentUser(user);
		pageableData.setPageSize(10);

	}

	@Test
	public void shouldGetUnitOrderList()
	{
		final B2BCustomerModel b2BCustomerModel = b2bCustomerService.getCurrentB2BCustomer();
		final Set<B2BUnitModel> b2bBranchUnitUids = b2bUnitService.getBranch(b2bUnitService.getParent(b2BCustomerModel));

		final SolrSearchQueryData solrSearchQueryData = new SolrSearchQueryData();
		solrSearchQueryData.setFilterTerms(List.of());

		SearchPageData<OrderModel> branchOrders = defaultB2BUnitOrderDao.findBranchOrdersByStore(b2bBranchUnitUids,
				baseStoreService.getCurrentBaseStore(), solrSearchQueryData, null, pageableData);

		assertThat(branchOrders).isNotNull();
		assertThat(branchOrders.getResults()).isNotNull();
		assertThat(branchOrders.getResults().size()).isEqualTo(6);
	}

	@Test
	public void shouldGetUnitOrderListWhenFilterByBuyerAndUnit()
	{
		final B2BCustomerModel b2BCustomerModel = b2bCustomerService.getCurrentB2BCustomer();
		final Set<B2BUnitModel> b2bBranchUnitUids = b2bUnitService.getBranch(b2bUnitService.getParent(b2BCustomerModel));

		var unitTermData = new SolrSearchQueryTermData();
		unitTermData.setKey(UNIT_FILTER_PARAM);
		unitTermData.setValue("middleUnitEast");
		var buyerTermData = new SolrSearchQueryTermData();
		buyerTermData.setKey(BUYER_FILTER_PARAM);
		buyerTermData.setValue("Test customer 2");

		final SolrSearchQueryData solrSearchQueryData = new SolrSearchQueryData();
		solrSearchQueryData.setFilterTerms(List.of(buyerTermData, unitTermData));

		SearchPageData<OrderModel> branchOrders = defaultB2BUnitOrderDao.findBranchOrdersByStore(b2bBranchUnitUids,
				baseStoreService.getCurrentBaseStore(), solrSearchQueryData, null, pageableData);

		assertThat(branchOrders).isNotNull();
		assertThat(branchOrders.getResults()).isNotNull();
		assertThat(branchOrders.getResults().stream()).extracting("code", "user.name", "unit.name")
				.containsExactly(tuple("testOrder5", "Test customer 2", "middleUnitEast"),
						tuple("testOrder2", "Test customer 2", "middleUnitEast"));

	}


	@Test
	public void shouldGetUnitOrderListInOrderWhenSortingByBuyer()
	{
		final B2BCustomerModel b2BCustomerModel = b2bCustomerService.getCurrentB2BCustomer();
		final Set<B2BUnitModel> b2bBranchUnitUids = b2bUnitService.getBranch(b2bUnitService.getParent(b2BCustomerModel));

		final SolrSearchQueryData solrSearchQueryData = new SolrSearchQueryData();
		solrSearchQueryData.setFilterTerms(List.of());
		pageableData.setSort("byBuyer");

		SearchPageData<OrderModel> branchOrdersByBuyer = defaultB2BUnitOrderDao.findBranchOrdersByStore(b2bBranchUnitUids,
				baseStoreService.getCurrentBaseStore(), solrSearchQueryData, null, pageableData);

		assertThat(branchOrdersByBuyer).isNotNull();
		assertThat(branchOrdersByBuyer.getResults()).isNotNull();
		assertThat(branchOrdersByBuyer.getResults().stream()).extracting("code", "user.name", "unit.name")
				.containsExactly(tuple("testOrder1", "Test customer 1", "rootUnit"),
						tuple("testOrder5", "Test customer 2", "middleUnitEast"),
						tuple("testOrder2", "Test customer 2", "middleUnitEast"), tuple("testOrder3", "Test customer 3", "childUnit"),
						tuple("testOrder4", "Test customer 4", "middleUnitEast"),
						tuple("testOrder6", "Test customer 4", "middleUnitEast"));

		pageableData.setSort("byBuyerDesc");
		SearchPageData<OrderModel> branchOrdersByBuyerDesc = defaultB2BUnitOrderDao.findBranchOrdersByStore(b2bBranchUnitUids,
				baseStoreService.getCurrentBaseStore(), solrSearchQueryData, null, pageableData);

		assertThat(branchOrdersByBuyerDesc).isNotNull();
		assertThat(branchOrdersByBuyerDesc.getResults()).isNotNull();
		assertThat(branchOrdersByBuyerDesc.getResults().stream()).extracting("code", "user.name", "unit.name")
				.containsExactly(tuple("testOrder4", "Test customer 4", "middleUnitEast"),
						tuple("testOrder6", "Test customer 4", "middleUnitEast"), tuple("testOrder3", "Test customer 3", "childUnit"),
						tuple("testOrder5", "Test customer 2", "middleUnitEast"),
						tuple("testOrder2", "Test customer 2", "middleUnitEast"), tuple("testOrder1", "Test customer 1", "rootUnit"));
	}

	@Test
	public void shouldGetUnitOrderListInOrderWhenSortingByUnit()
	{
		final B2BCustomerModel b2BCustomerModel = b2bCustomerService.getCurrentB2BCustomer();
		final Set<B2BUnitModel> b2bBranchUnitUids = b2bUnitService.getBranch(b2bUnitService.getParent(b2BCustomerModel));

		final SolrSearchQueryData solrSearchQueryData = new SolrSearchQueryData();
		solrSearchQueryData.setFilterTerms(List.of());
		pageableData.setSort("byOrgUnit");

		SearchPageData<OrderModel> branchOrdersByUnit = defaultB2BUnitOrderDao.findBranchOrdersByStore(b2bBranchUnitUids,
				baseStoreService.getCurrentBaseStore(), solrSearchQueryData, null, pageableData);

		assertThat(branchOrdersByUnit).isNotNull();
		assertThat(branchOrdersByUnit.getResults()).isNotNull();
		assertThat(branchOrdersByUnit.getResults()).extracting("code", "user.uid", "unit.name")
				.containsExactly(tuple("testOrder3", "customer3", "childUnit"), tuple("testOrder5", "customer2", "middleUnitEast"),
						tuple("testOrder2", "customer2", "middleUnitEast"), tuple("testOrder4", "customer4", "middleUnitEast"),
						tuple("testOrder6", "customer4", "middleUnitEast"), tuple("testOrder1", "customer1", "rootUnit"));

		pageableData.setSort("byOrgUnitDesc");
		SearchPageData<OrderModel> branchOrdersByUnitDesc = defaultB2BUnitOrderDao.findBranchOrdersByStore(b2bBranchUnitUids,
				baseStoreService.getCurrentBaseStore(), solrSearchQueryData, null, pageableData);

		assertThat(branchOrdersByUnitDesc).isNotNull();
		assertThat(branchOrdersByUnitDesc.getResults()).isNotNull();
		assertThat(branchOrdersByUnitDesc.getResults()).extracting("code", "user.uid", "unit.name")
				.containsExactly(tuple("testOrder1", "customer1", "rootUnit"), tuple("testOrder5", "customer2", "middleUnitEast"),
						tuple("testOrder2", "customer2", "middleUnitEast"), tuple("testOrder4", "customer4", "middleUnitEast"),
						tuple("testOrder6", "customer4", "middleUnitEast"), tuple("testOrder3", "customer3", "childUnit"));
	}
}
