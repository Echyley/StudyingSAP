/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang3.CharEncoding;
import org.junit.Before;
import org.junit.Test;

import com.hybris.merchandising.model.MerchSynchronizationModel;

@IntegrationTest
public class DefaultMerchSynchronizationDaoIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String OPERATION_ID_1 = "operationId1";
	private static final String NOT_EXISTING_OPERATION_ID = "notExisting";

	@Resource(name = "defaultMerchSynchronizationDao")
	private DefaultMerchSynchronizationDao dao;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/test/integration/merchSyncData.impex", CharEncoding.UTF_8);
	}

	@Test
	public void testFindByOperationId()
	{
		//when
		final Optional<MerchSynchronizationModel> result = dao.findByOperationId(OPERATION_ID_1);

		//then
		assertNotNull(result);
		assertTrue(result.isPresent());
		final MerchSynchronizationModel model = result.get();
		assertEquals(OPERATION_ID_1, model.getOperationId());
	}

	@Test
	public void testFindByOperationIdWhenNotExists()
	{
		//when
		final Optional<MerchSynchronizationModel> result = dao.findByOperationId(NOT_EXISTING_OPERATION_ID);

		//then
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

}
