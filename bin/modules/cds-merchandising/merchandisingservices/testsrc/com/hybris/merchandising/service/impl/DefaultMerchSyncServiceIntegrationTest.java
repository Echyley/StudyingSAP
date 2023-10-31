/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.Optional;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import com.hybris.merchandising.model.MerchProductDirectoryConfigModel;
import com.hybris.merchandising.model.MerchSnConfigModel;
import com.hybris.merchandising.model.MerchSynchronizationModel;
import com.hybris.merchandising.service.MerchProductDirectoryConfigService;
import com.hybris.merchandising.service.MerchSnConfigService;
import com.hybris.merchandising.service.impl.DefaultMerchSyncService.MerchSyncStatus;

@IntegrationTest
public class DefaultMerchSyncServiceIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String OPERATION_ID_1 = "operationId1";
	private static final String OPERATION_ID_2 = "operationId2";
	private static final String NEW_OPERATION_ID = "newOperationId";
	private static final String NOT_EXISTING_OPERATION_ID = "notExisting";
	private static final String FULL = "FULL";
	private static final String MESSAGE = "log message";

	private static final String INDEXED_TYPE_1_ID = "solrIndexedType1";
	private static final String INDEX_TYPE_ID_1 = "snIndexedType1";

	@Resource(name = "defaultMerchSyncService")
	private DefaultMerchSyncService service;

	@Resource
	private MerchProductDirectoryConfigService merchProductDirectoryConfigService;
	@Resource
	private MerchSnConfigService merchSnConfigService;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/test/integration/merchSyncData.impex", "utf-8");
	}

	@Test
	public void testCreateMerchSychronization()
	{
		//given
		final MerchProductDirectoryConfigModel merchConfig = merchProductDirectoryConfigService
				.getMerchProductDirectoryConfigForIndexedType(INDEXED_TYPE_1_ID).get();

		//when
		final MerchSynchronizationModel result = service.createMerchSychronization(merchConfig, NEW_OPERATION_ID, FULL);

		//then
		assertNotNull(result);
		assertEquals(NEW_OPERATION_ID, result.getOperationId());
		assertEquals(merchConfig, result.getConfig());
		assertEquals(FULL, result.getType());
		assertEquals(MerchSyncStatus.STARTED.name(), result.getStatus());
		assertNotNull(result.getStartTime());
		assertNull(result.getEndTime());
	}

	@Test
	public void testCreateMerchSychronizationForMerchSnConfig()
	{
		//given
		final MerchSnConfigModel merchConfig = merchSnConfigService.getMerchConfigForIndexedType(INDEX_TYPE_ID_1).get();

		//when
		final MerchSynchronizationModel result = service.createMerchSychronization(merchConfig, NEW_OPERATION_ID, FULL);

		//then
		assertNotNull(result);
		assertEquals(NEW_OPERATION_ID, result.getOperationId());
		assertEquals(merchConfig, result.getMerchSnConfig());
		assertEquals(FULL, result.getType());
		assertEquals(MerchSyncStatus.STARTED.name(), result.getStatus());
		assertNotNull(result.getStartTime());
		assertNull(result.getEndTime());
	}

	@Test
	public void testGetMerchSynchronization()
	{
		//when
		final Optional<MerchSynchronizationModel> result = service.getMerchSynchronization(OPERATION_ID_1);

		//then
		assertNotNull(result);
		assertTrue(result.isPresent());
		assertEquals(OPERATION_ID_1, result.get().getOperationId());
	}

	@Test
	public void testGetMerchSynchronizationWhenNotExist()
	{
		//when
		final Optional<MerchSynchronizationModel> result = service.getMerchSynchronization(NOT_EXISTING_OPERATION_ID);

		//then
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testIsMerchSyncFailedForFailedSync()
	{
		//when
		final boolean result = service.isMerchSyncFailed(OPERATION_ID_2);

		//then
		assertTrue(result);
	}

	@Test
	public void testIsMerchSyncFailedForCompletedSync()
	{
		//when
		final boolean result = service.isMerchSyncFailed(OPERATION_ID_1);

		//then
		assertFalse(result);
	}

	@Test
	public void testIsMerchSyncFailedForNotExisting()
	{
		//when
		final boolean result = service.isMerchSyncFailed(NOT_EXISTING_OPERATION_ID);

		//then
		assertFalse(result);
	}

	@Test
	public void testCompleteSyncProcess()
	{
		//given
		final MerchProductDirectoryConfigModel merchConfig = merchProductDirectoryConfigService
				.getMerchProductDirectoryConfigForIndexedType(INDEXED_TYPE_1_ID).get();
		final MerchSynchronizationModel newMerchSync = service.createMerchSychronization(merchConfig, NEW_OPERATION_ID, FULL);
		assertEquals(MerchSyncStatus.STARTED.name(), newMerchSync.getStatus());
		assertNull(newMerchSync.getEndTime());

		//when
		service.completeMerchSyncProcess(NEW_OPERATION_ID, 10L);

		//then
		assertEquals(MerchSyncStatus.COMPLETED.name(), newMerchSync.getStatus());
		assertEquals(Long.valueOf(10L), newMerchSync.getNumberOfProducts());
		assertNotNull(newMerchSync.getEndTime());
	}

	@Test
	public void testCompleteSyncProcessWhenFailed()
	{
		//given
		final MerchProductDirectoryConfigModel merchConfig = merchProductDirectoryConfigService
				.getMerchProductDirectoryConfigForIndexedType(INDEXED_TYPE_1_ID).get();
		final MerchSynchronizationModel newMerchSync = service.createMerchSychronization(merchConfig, NEW_OPERATION_ID, FULL);
		service.saveErrorInfo(NEW_OPERATION_ID, "Error message", new Exception("exception"));
		assertEquals(MerchSyncStatus.FAILED.name(), newMerchSync.getStatus());
		assertNull(newMerchSync.getEndTime());

		//when
		service.completeMerchSyncProcess(NEW_OPERATION_ID, 0L);

		//then
		assertEquals(MerchSyncStatus.FAILED.name(), newMerchSync.getStatus());
		assertEquals(Long.valueOf(0), newMerchSync.getNumberOfProducts());
		assertNotNull(newMerchSync.getEndTime());
	}

	@Test
	public void testSaveErrorInfo()
	{
		//given
		final Optional<MerchSynchronizationModel> merchSyncOptional = service.getMerchSynchronization(OPERATION_ID_1);
		assertTrue(merchSyncOptional.isPresent());
		final MerchSynchronizationModel merchSync = merchSyncOptional.get();
		assertFalse(MerchSyncStatus.FAILED.name().equals(merchSync.getStatus()));
		final Exception e = new Exception("exception");

		//when
		service.saveErrorInfo(OPERATION_ID_1, MESSAGE, e);

		//then
		assertEquals(MerchSyncStatus.FAILED.name(), merchSync.getStatus());
		assertNotNull(merchSync.getDetails());
		final String details = merchSync.getDetails();
		assertNotNull(details);
		assertTrue(details.contains(MESSAGE));
		assertTrue(details.contains(e.getMessage()));
	}

}
