/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.commerceservices.multisite.interceptors;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.multisite.MultiSiteUidDecorationService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import javax.annotation.Resource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


@IntegrationTest
public class MultiSiteCustomerInitInterceptorTest extends ServicelayerTransactionalTest
{
	private static final String DEFAULT_TEST_BASE_SITE_UID = "testBaseSiteUID";
	private static final String DEFAULT_TEST_BASE_SITE_NAME = "testBaseSiteName";
	private static final String DEFAULT_TEST_CUSTOMER_UID = "test@sap.com";
	@Resource
	private ModelService modelService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private MultiSiteUidDecorationService multiSiteUidDecorationService;
	@Resource
	private UserService userService;

	@Test
	public void testRegisterNewCustomerToSiteWithDataIsolationEnabled()
	{
		prepareCurrentBaseSite(true);
		final CustomerModel customerModel = modelService.create(CustomerModel.class);
		customerModel.setUid(DEFAULT_TEST_CUSTOMER_UID);

		modelService.save(customerModel);

		final CustomerModel fetchedCustomer = userService.getUserForUID(DEFAULT_TEST_CUSTOMER_UID, CustomerModel.class);

		assertEquals(multiSiteUidDecorationService.decorate(DEFAULT_TEST_CUSTOMER_UID, DEFAULT_TEST_BASE_SITE_UID),
				fetchedCustomer.getUid());
		assertNotNull(fetchedCustomer.getSite());
		assertEquals(DEFAULT_TEST_BASE_SITE_UID, fetchedCustomer.getSite().getUid());
	}

	@Test
	public void testRegisterNewCustomerToSiteWithDataIsolationDisabled()
	{
		prepareCurrentBaseSite(false);
		final CustomerModel customerModel = modelService.create(CustomerModel.class);
		customerModel.setUid(DEFAULT_TEST_CUSTOMER_UID);

		modelService.save(customerModel);

		final CustomerModel fetchedCustomer = userService.getUserForUID(DEFAULT_TEST_CUSTOMER_UID, CustomerModel.class);

		assertEquals(DEFAULT_TEST_CUSTOMER_UID, fetchedCustomer.getUid());
		assertNull(fetchedCustomer.getSite());
	}

	@Test
	public void testRegisterNewCustomerWhenCurrentSiteIsNull()
	{
		baseSiteService.setCurrentBaseSite((BaseSiteModel) null, false);

		final CustomerModel customerModel = modelService.create(CustomerModel.class);
		customerModel.setUid(DEFAULT_TEST_CUSTOMER_UID);

		modelService.save(customerModel);

		final CustomerModel fetchedCustomer = userService.getUserForUID(DEFAULT_TEST_CUSTOMER_UID, CustomerModel.class);

		assertEquals(DEFAULT_TEST_CUSTOMER_UID, fetchedCustomer.getUid());
		assertNull(fetchedCustomer.getSite());
	}

	protected void prepareCurrentBaseSite(final boolean dataIsolationEnabled)
	{
		final BaseSiteModel currentBaseSite = modelService.create(BaseSiteModel.class);
		currentBaseSite.setUid(DEFAULT_TEST_BASE_SITE_UID);
		currentBaseSite.setName(DEFAULT_TEST_BASE_SITE_NAME);
		currentBaseSite.setDataIsolationEnabled(dataIsolationEnabled);

		modelService.save(currentBaseSite);

		baseSiteService.setCurrentBaseSite(currentBaseSite, false);
	}

}
