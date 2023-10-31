/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedserviceservices.dao.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.assistedserviceservices.dao.CustomerGroupDao;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.user.StoreEmployeeGroupModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Test class for {@link DefaultCustomerGroupDao}
 */
@IntegrationTest
public class DefaultCustomerGroupDaoTest extends ServicelayerTransactionalTest
{
	@Resource
	private CustomerGroupDao customerGroupDao;

	@Resource
	private UserService userService;

	@Resource
	private BaseSiteService baseSiteService;

	private PageableData pageableData;

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
	public void nakanoStoreCustomersEmptyPaginationTest()
	{
		customerGroupDao.findAllCustomersByGroups(new ArrayList(), null);
	}

	@Test
	public void nakanoStoreCustomersTest()
	{
		final List<UserGroupModel> userGroups = new ArrayList<UserGroupModel>();
		final UserGroupModel customerUserGroup = userService.getUserGroupForUID("POS_NAKANO");

		assertNotNull(customerUserGroup);

		userGroups.add(customerUserGroup);

		final SearchPageData<CustomerModel> customers = customerGroupDao.findAllCustomersByGroups(userGroups, pageableData);
		assertEquals(1, customers.getResults().size());
	}

	@Test
	public void ichikawaStoreCustomersTest()
	{
		final List<UserGroupModel> userGroups = new ArrayList<UserGroupModel>();
		final UserGroupModel customerUserGroup = userService.getUserGroupForUID("POS_ICHIKAWA");

		assertNotNull(customerUserGroup);

		userGroups.add(customerUserGroup);
		SearchPageData<CustomerModel> customers = customerGroupDao.findAllCustomersByGroups(userGroups, pageableData);
		assertEquals(2, customers.getResults().size());

		// test with query
		final Map query = new HashMap<String, String>();
		query.put("query", "test_user2");
		customers = customerGroupDao.findAllCustomersByGroups(userGroups, pageableData, query);
		assertEquals(1, customers.getResults().size());
		assertEquals("test_user2@hybris.com", customers.getResults().get(0).getUid());

		// test with query 2, case insensitive
		query.clear();
		query.put("query", "TEST_uSer2");
		customers = customerGroupDao.findAllCustomersByGroups(userGroups, pageableData, query);
		assertEquals(1, customers.getResults().size());
		assertEquals("test_user2@hybris.com", customers.getResults().get(0).getUid());

	}

	@Test
	public void ichikawaStoreCustomersTestIsolated()
	{
		final List<UserGroupModel> userGroups = new ArrayList<UserGroupModel>();
		final UserGroupModel customerUserGroup = userService.getUserGroupForUID("POS_ICHIKAWA");

		assertNotNull(customerUserGroup);

		userGroups.add(customerUserGroup);
		baseSite.setDataIsolationEnabled(true);
		final SearchPageData<CustomerModel> customers = customerGroupDao.findAllCustomersByGroups(userGroups, pageableData);
		assertEquals(0, customers.getResults().size());
	}

	@Test
	public void ichikawaAndNakanoStoreCustomersTest()
	{
		final List<UserGroupModel> userGroups = new ArrayList<UserGroupModel>();
		final UserGroupModel ichikawaCustomerUserGroup = userService.getUserGroupForUID("POS_ICHIKAWA");
		final UserGroupModel nakanoCustomerUserGroup = userService.getUserGroupForUID("POS_NAKANO");

		assertNotNull(ichikawaCustomerUserGroup);
		assertNotNull(nakanoCustomerUserGroup);

		userGroups.add(ichikawaCustomerUserGroup);
		userGroups.add(nakanoCustomerUserGroup);

		final SearchPageData<CustomerModel> customers = customerGroupDao.findAllCustomersByGroups(userGroups, pageableData);
		assertEquals(3, customers.getResults().size());
	}

	@Test
	public void getCustomersByNakanoPosTest()
	{
		final List<UserGroupModel> userGroups = new ArrayList<UserGroupModel>();
		final StoreEmployeeGroupModel nakanoEmployeeGroup = userService.getUserGroupForUID("nakanostoreemployees", StoreEmployeeGroupModel.class);

		assertNotNull(nakanoEmployeeGroup);

		userGroups.add(nakanoEmployeeGroup);

		SearchPageData<CustomerModel> customers = customerGroupDao.findAllCustomersByConsignmentsInPointOfServices(Collections.singletonList(nakanoEmployeeGroup.getStore()), pageableData);

		assertEquals(2, customers.getResults().size());
		assertTrue(customers.getResults().stream().map(CustomerModel::getCustomerID).collect(Collectors.toList()).contains("user2@test.net"));
		assertTrue(customers.getResults().stream().map(CustomerModel::getCustomerID).collect(Collectors.toList()).contains("user1@test.net"));

		// test with query
		final Map query = new HashMap<String, String>();
		query.put("query", "user1");
		customers = customerGroupDao.findAllCustomersByConsignmentsInPointOfServices(Collections.singletonList(nakanoEmployeeGroup.getStore()), pageableData, query);
		assertEquals(1, customers.getResults().size());
		assertEquals("user1", customers.getResults().get(0).getUid());

		// test with query 2
		query.clear();
		query.put("query", "usER1");
		customers = customerGroupDao.findAllCustomersByConsignmentsInPointOfServices(Collections.singletonList(nakanoEmployeeGroup.getStore()), pageableData, query);
		assertEquals(1, customers.getResults().size());
		assertEquals("user1", customers.getResults().get(0).getUid());
	}

	@Test
	public void getCustomersByIchikawaPosTest()
	{
		final List<UserGroupModel> userGroups = new ArrayList<UserGroupModel>();
		final StoreEmployeeGroupModel ichikawaEmployeeGroup = userService.getUserGroupForUID("ichikawastoreemployees", StoreEmployeeGroupModel.class);

		assertNotNull(ichikawaEmployeeGroup);

		userGroups.add(ichikawaEmployeeGroup);

		final SearchPageData<CustomerModel> customers = customerGroupDao.findAllCustomersByConsignmentsInPointOfServices(Collections.singletonList(ichikawaEmployeeGroup.getStore()), pageableData);

		assertEquals(1, customers.getResults().size());
		assertTrue(customers.getResults().stream().map(CustomerModel::getCustomerID).collect(Collectors.toList()).contains("user2@test.net"));
	}

	@Test(expected = UnknownIdentifierException.class)
	public void invalidCustomerGroup()
	{
		userService.getUserGroupForUID("ICHIKAWA");
	}
}
