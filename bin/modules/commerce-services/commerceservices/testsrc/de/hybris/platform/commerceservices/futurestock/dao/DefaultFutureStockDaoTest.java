/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.futurestock.dao;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commerceservices.futurestock.dao.impl.DefaultFutureStockDao;
import de.hybris.platform.commerceservices.model.FutureStockModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

import javax.annotation.Resource;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * JUnit test suite for {@link DefaultFutureStockDao}
 */
@IntegrationTest
public class DefaultFutureStockDaoTest extends ServicelayerTest
{
	private static final String TEST_PRO_CODE = "testProduct0";
	@Resource
	private FutureStockDao futureStockDao;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/commerceservices/test/testFutureStocks.impex", "utf-8");
	}

	@Test
	public void shouldFutureStocksByProductCode()
	{
		final List<FutureStockModel> futureStocks = futureStockDao.getFutureStocksByProductCode(TEST_PRO_CODE);
		Assert.assertEquals(2, futureStocks.size());
		Assert.assertEquals(TEST_PRO_CODE, futureStocks.get(0).getProductCode());
	}
}
