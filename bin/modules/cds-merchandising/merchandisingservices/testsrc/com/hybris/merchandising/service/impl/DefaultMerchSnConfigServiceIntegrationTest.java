/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.searchservices.admin.dao.SnIndexTypeDao;
import de.hybris.platform.searchservices.model.SnIndexTypeModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.site.BaseSiteService;

import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.hybris.merchandising.model.MerchSnConfigModel;

@IntegrationTest
public class DefaultMerchSnConfigServiceIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String INDEX_TYPE_1_ID = "snIndexType1";
	private static final String INDEX_TYPE_3_ID = "snIndexType3";

	private static final String ELECTRONICS = "electronics";

	private static final String UPDATED_VALUE = "new value";

	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private SnIndexTypeDao snIndexTypeDao;

	@Resource(name = "defaultMerchSnConfigService")
	private DefaultMerchSnConfigService service;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/test/integration/merchSnConfigData.impex", "utf-8");
	}

	@Test
	public void testGetMerchConfigForIndexType()
	{
		//when
		final Optional<MerchSnConfigModel> result = service.getMerchConfigForIndexedType(INDEX_TYPE_1_ID);

		//then
		assertNotNull(result);
		assertTrue(result.isPresent());
		final MerchSnConfigModel model = result.get();
		assertNotNull(model.getSnIndexType());
		assertEquals(INDEX_TYPE_1_ID, model.getSnIndexType().getId());
	}

	@Test
	public void testGetMerchConfigForIndexTypeWhenNotExist()
	{
		//when
		final Optional<MerchSnConfigModel> result = service.getMerchConfigForIndexedType(INDEX_TYPE_3_ID);

		//then
		assertNotNull(result);
		assertFalse(result.isPresent());
	}

	@Test
	public void testGetMerchConfigForIndexTypeModel()
	{
		//given
		final Optional<SnIndexTypeModel> indexTypeModel = snIndexTypeDao.findIndexTypeById(INDEX_TYPE_1_ID);

		//when
		final Optional<MerchSnConfigModel> result = service.getMerchConfigForIndexedType(indexTypeModel.get());

		//then
		assertNotNull(result);
		assertTrue(result.isPresent());
		final MerchSnConfigModel model = result.get();
		assertNotNull(model.getSnIndexType());
		assertEquals(INDEX_TYPE_1_ID, model.getSnIndexType().getId());
	}

	@Test
	public void testGetMerchConfigForIndexTypenModelWhenNotExist()
	{
		//given
		final Optional<SnIndexTypeModel> indexTypeModel = snIndexTypeDao.findIndexTypeById(INDEX_TYPE_3_ID);

		//when
		final Optional<MerchSnConfigModel> result = service.getMerchConfigForIndexedType(indexTypeModel.get());

		//then
		assertNotNull(result);
		assertFalse(result.isPresent());
	}

	@Test
	public void testGetMerchConfigForCurrentBaseSite()
	{
		//given
		baseSiteService.setCurrentBaseSite(ELECTRONICS, false);

		//when
		final Optional<MerchSnConfigModel> result = service.getMerchConfigForCurrentBaseSite();

		//then
		assertNotNull(result);
		assertTrue(result.isPresent());
		final MerchSnConfigModel model = result.get();
		assertNotNull(model.getBaseSite());
		assertEquals(ELECTRONICS, model.getBaseSite().getUid());
	}

	@Test
	public void testGetMerchConfigForCurrentBaseSiteWhenNotExist()
	{
		//given
		baseSiteService.setCurrentBaseSite("apparel-uk", false);

		//when
		final Optional<MerchSnConfigModel> result = service.getMerchConfigForCurrentBaseSite();

		//then
		assertNotNull(result);
		assertFalse(result.isPresent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetMerchConfigForCurrentBaseSiteWhenNoCurrentBaseSite()
	{
		//when
		service.getMerchConfigForCurrentBaseSite();
	}

	@Test
	public void testUpdateMerchConfig()
	{
		//given
		final SnIndexTypeModel indexTypeModel = snIndexTypeDao.findIndexTypeById(INDEX_TYPE_1_ID).get();
		final MerchSnConfigModel config = service.getMerchSnConfigDao().findByIndexedType(indexTypeModel).get();
		config.setDisplayName(UPDATED_VALUE);
		config.setBaseImageUrl(UPDATED_VALUE);

		//when
		service.updateMerchConfig(config);
		final MerchSnConfigModel updated = service.getMerchConfigForIndexedType(indexTypeModel).get();

		//then
		assertEquals(UPDATED_VALUE, updated.getDisplayName());
		assertEquals(UPDATED_VALUE, updated.getBaseImageUrl());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateMerchConfigWhenNull()
	{
		//when
		service.updateMerchConfig(null);
	}
}
