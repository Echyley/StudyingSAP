/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.searchservices.admin.dao.SnIndexTypeDao;
import de.hybris.platform.searchservices.model.SnIndexTypeModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.site.BaseSiteService;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.hybris.merchandising.model.MerchSnConfigModel;

@IntegrationTest
public class DefaultMerchSnConfigDaoIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String INDEX_TYPE_1_ID = "snIndexType1";
	private static final String INDEX_TYPE_3_ID = "snIndexType3";

	private static final String ELECTRONICS = "electronics";

	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private SnIndexTypeDao snIndexTypeDao;

	@Resource(name = "defaultMerchSnConfigDao")
	private DefaultMerchSnConfigDao dao;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/test/integration/merchSnConfigData.impex", "utf-8");
	}

	@Test
	public void testFindAll()
	{
		//when
		final Collection<MerchSnConfigModel> result = dao.findAll();

		//then
		assertNotNull(result);
		assertEquals(2, result.size());
	}

	@Test
	public void testFindByIndexType()
	{
		//given
		final Optional<SnIndexTypeModel> indexTypeModel = snIndexTypeDao.findIndexTypeById(INDEX_TYPE_1_ID);

		//when
		final Optional<MerchSnConfigModel> result = dao.findByIndexedType(indexTypeModel.get());

		//then
		assertNotNull(result);
		assertTrue(result.isPresent());
		final MerchSnConfigModel model = result.get();
		assertNotNull(model.getSnIndexType());
		assertEquals(INDEX_TYPE_1_ID, model.getSnIndexType().getId());
	}

	@Test
	public void testFindByIndexTypeWhenNotExist()
	{
		//given
		final Optional<SnIndexTypeModel> indexTypeModel = snIndexTypeDao.findIndexTypeById(INDEX_TYPE_3_ID);

		//when
		final Optional<MerchSnConfigModel> result = dao.findByIndexedType(indexTypeModel.get());

		//then
		assertNotNull(result);
		assertFalse(result.isPresent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindByIndexTypeWhenNull()
	{
		//when
		dao.findByIndexedType(null);
	}

	@Test
	public void testFindByBaseSite()
	{
		//given
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID(ELECTRONICS);

		//when
		final Optional<MerchSnConfigModel> result = dao.findByBaseSite(baseSite);

		//then
		assertNotNull(result);
		assertTrue(result.isPresent());
		final MerchSnConfigModel model = result.get();
		assertNotNull(model.getBaseSite());
		assertEquals(ELECTRONICS, model.getBaseSite().getUid());
	}

	@Test
	public void testFindByBaseSiteWhenNotExist()
	{
		//given
		final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID("apparel-uk");

		//when
		final Optional<MerchSnConfigModel> result = dao.findByBaseSite(baseSite);

		//then
		assertNotNull(result);
		assertFalse(result.isPresent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindByBaseSiteWhenNull()
	{
		//when
		dao.findByBaseSite(null);
	}
}
