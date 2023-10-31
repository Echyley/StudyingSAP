/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.commerceservices.multisite.interceptors;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

import javax.annotation.Resource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


@IntegrationTest
public class MultiSiteBaseSiteInterceptorTest extends ServicelayerTransactionalTest
{
	private static final String DEFAULT_BASE_SITE_UID = "defaultBaseSiteUid";
	private static final String CHANGED_BASE_SITE_UID = "changedBaseSiteUid";
	private static final String DEFAULT_CUSTOMER_UID = "defaultCustomerUid";
	private static final String DEFAULT_CUSTOMER_NAME = "defaultCustomerName";

	@Resource
	private ModelService modelService;
	@Resource
	private BaseSiteService baseSiteService;

	@Test
	public void testChangeUidWithDataIsolationDisabledWillSuccess()
	{
		// given
		final BaseSiteModel baseSiteModel = modelService.create(BaseSiteModel.class);

		baseSiteModel.setUid(DEFAULT_BASE_SITE_UID);
		baseSiteModel.setDataIsolationEnabled(Boolean.FALSE);

		modelService.save(baseSiteModel);

		// when
		baseSiteModel.setUid(CHANGED_BASE_SITE_UID);

		// then
		modelService.save(baseSiteModel);

		// expect
		final BaseSiteModel changedBaseSite = baseSiteService.getBaseSiteForUID(CHANGED_BASE_SITE_UID);
		assertNotNull(changedBaseSite);
		assertEquals(CHANGED_BASE_SITE_UID, changedBaseSite.getUid());
	}

	@Test
	public void testChangeUidWithDataIsolationEnabledWithoutCustomerAssignedWillSuccess()
	{
		// given
		final BaseSiteModel baseSiteModel = modelService.create(BaseSiteModel.class);

		baseSiteModel.setUid(DEFAULT_BASE_SITE_UID);
		baseSiteModel.setDataIsolationEnabled(Boolean.TRUE);

		modelService.save(baseSiteModel);

		// when
		baseSiteModel.setUid(CHANGED_BASE_SITE_UID);

		// then
		modelService.save(baseSiteModel);

		// expect
		final BaseSiteModel changedBaseSite = baseSiteService.getBaseSiteForUID(CHANGED_BASE_SITE_UID);
		assertNotNull(changedBaseSite);
		assertEquals(CHANGED_BASE_SITE_UID, changedBaseSite.getUid());
	}

	@Test(expected = ModelSavingException.class)
	public void testChangeUidWithDataIsolationEnabledWithCustomerAssignedWillFail() {
		// given
		final BaseSiteModel baseSiteModel = modelService.create(BaseSiteModel.class);

		baseSiteModel.setUid(DEFAULT_BASE_SITE_UID);
		baseSiteModel.setDataIsolationEnabled(Boolean.TRUE);

		modelService.save(baseSiteModel);

		final CustomerModel customerModel = modelService.create(CustomerModel.class);
		customerModel.setUid(DEFAULT_CUSTOMER_UID);
		customerModel.setName(DEFAULT_CUSTOMER_NAME);
		customerModel.setSite(baseSiteModel);

		modelService.save(customerModel);

		// when
		baseSiteModel.setUid(CHANGED_BASE_SITE_UID);

		// then
		modelService.save(baseSiteModel);
	}

	@Test
	public void testRemoveBaseSiteWithDataIsolationDisabledWillSuccess()
	{
		// given
		final BaseSiteModel baseSiteModel = modelService.create(BaseSiteModel.class);

		baseSiteModel.setUid(DEFAULT_BASE_SITE_UID);
		baseSiteModel.setDataIsolationEnabled(Boolean.FALSE);

		modelService.save(baseSiteModel);

		// when
		modelService.remove(baseSiteModel);

		// expected
		final BaseSiteModel removedBaseSite = baseSiteService.getBaseSiteForUID(CHANGED_BASE_SITE_UID);
		assertNull(removedBaseSite);
	}

	@Test
	public void testRemoveBaseSiteWithDataIsolationEnabledWithoutCustomerWillSuccess()
	{
		// given
		final BaseSiteModel baseSiteModel = modelService.create(BaseSiteModel.class);

		baseSiteModel.setUid(DEFAULT_BASE_SITE_UID);
		baseSiteModel.setDataIsolationEnabled(Boolean.TRUE);

		modelService.save(baseSiteModel);

		// when
		modelService.remove(baseSiteModel);

		// expected
		final BaseSiteModel removedBaseSite = baseSiteService.getBaseSiteForUID(CHANGED_BASE_SITE_UID);
		assertNull(removedBaseSite);
	}

	@Test(expected = ModelRemovalException.class)
	public void testRemoveBaseSiteWithDataIsolationEnabledWithCustomerWillSuccess()
	{
		// given
		final BaseSiteModel baseSiteModel = modelService.create(BaseSiteModel.class);

		baseSiteModel.setUid(DEFAULT_BASE_SITE_UID);
		baseSiteModel.setDataIsolationEnabled(Boolean.TRUE);

		modelService.save(baseSiteModel);

		final CustomerModel customerModel = modelService.create(CustomerModel.class);
		customerModel.setUid(DEFAULT_CUSTOMER_UID);
		customerModel.setName(DEFAULT_CUSTOMER_NAME);
		customerModel.setSite(baseSiteModel);

		modelService.save(customerModel);

		// when
		modelService.remove(baseSiteModel);
	}
}
