/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.service.impl;

import static com.hybris.merchandising.service.impl.DefaultMerchSyncService.MERCH_SYNC_SUCCESSFUL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.merchandising.dao.MerchSynchronizationDao;
import com.hybris.merchandising.model.MerchProductDirectoryConfigModel;
import com.hybris.merchandising.model.MerchSnConfigModel;
import com.hybris.merchandising.model.MerchSynchronizationModel;
import com.hybris.merchandising.service.impl.DefaultMerchSyncService.MerchSyncStatus;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMerchSyncServiceUnitTest
{
	private static final String OPERATION_ID_1 = "operationId1";
	private static final String NEW_OPERATION_ID = "newOperationId";
	private static final String NOT_EXISTING_OPERATION_ID = "notExisting";
	private static final String FULL = "FULL";
	private static final Long NUMBER_OF_PRODUCTS = Long.valueOf(100L);
	private static final String MESSAGE = "message";

	@Mock
	private MerchSynchronizationDao merchSynchronizationDao;
	@Mock
	private TimeService timeService;
	@Mock
	private ModelService modelService;

	@InjectMocks
	private DefaultMerchSyncService service;

	@Mock
	private MerchSynchronizationModel merchSyncModel;
	@Mock
	private MerchProductDirectoryConfigModel merchConfig;
	@Mock
	private MerchSnConfigModel merchSnConfig;

	@Test
	public void testCreateMerchSychronization()
	{
		//given
		when(modelService.create(MerchSynchronizationModel.class)).thenReturn(merchSyncModel);

		//when
		final MerchSynchronizationModel result = service.createMerchSychronization(merchConfig, NEW_OPERATION_ID, FULL);

		//then
		assertNotNull(result);
		assertEquals(merchSyncModel, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateMerchSychronizationWhenNullConfig()
	{
		//when
		service.createMerchSychronization((MerchProductDirectoryConfigModel) null, NEW_OPERATION_ID, FULL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateMerchSychronizationWhenNullType()
	{
		//when
		service.createMerchSychronization(merchConfig, NEW_OPERATION_ID, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateMerchSychronizationWhenNullOperationId()
	{
		//when
		service.createMerchSychronization(merchConfig, null, FULL);
	}

	@Test
	public void testCreateMerchSychronizationForMerchSnConfig()
	{
		//given
		when(modelService.create(MerchSynchronizationModel.class)).thenReturn(merchSyncModel);

		//when
		final MerchSynchronizationModel result = service.createMerchSychronization(merchSnConfig, NEW_OPERATION_ID, FULL);

		//then
		assertNotNull(result);
		assertEquals(merchSyncModel, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateMerchSychronizationForMerchSnConfigWhenNullConfig()
	{
		//when
		service.createMerchSychronization((MerchSnConfigModel) null, NEW_OPERATION_ID, FULL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateMerchSychronizationForMerchSnConfigWhenNullType()
	{
		//when
		service.createMerchSychronization(merchSnConfig, NEW_OPERATION_ID, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateMerchSychronizationForMerchSnConfigWhenNullOperationId()
	{
		//when
		service.createMerchSychronization(merchSnConfig, null, FULL);
	}

	@Test
	public void testGetMerchSynchronization()
	{
		//given
		when(merchSynchronizationDao.findByOperationId(OPERATION_ID_1)).thenReturn(Optional.of(merchSyncModel));

		//when
		final Optional<MerchSynchronizationModel> result = service.getMerchSynchronization(OPERATION_ID_1);

		//then
		assertEquals(merchSyncModel, result.get());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetMerchSynchronizationWhenNullOperationId()
	{
		//when
		final Optional<MerchSynchronizationModel> result = service.getMerchSynchronization(null);
	}

	@Test
	public void testCompleteSyncProcess()
	{
		//given
		when(merchSynchronizationDao.findByOperationId(OPERATION_ID_1)).thenReturn(Optional.of(merchSyncModel));
		when(merchSyncModel.getStatus()).thenReturn(MerchSyncStatus.STARTED.name());

		//when
		service.completeMerchSyncProcess(OPERATION_ID_1, NUMBER_OF_PRODUCTS);

		//then
		verify(merchSyncModel).setStatus(MerchSyncStatus.COMPLETED.name());
		verify(merchSyncModel).setDetails(MERCH_SYNC_SUCCESSFUL);
		verify(merchSyncModel).setNumberOfProducts(NUMBER_OF_PRODUCTS);
		verify(merchSyncModel).setEndTime(any());
		verify(modelService).save(merchSyncModel);
	}

	@Test
	public void testCompleteSyncProcessWhenFailedStatus()
	{
		//given
		when(merchSynchronizationDao.findByOperationId(OPERATION_ID_1)).thenReturn(Optional.of(merchSyncModel));
		when(merchSyncModel.getStatus()).thenReturn(MerchSyncStatus.FAILED.name());

		//when
		service.completeMerchSyncProcess(OPERATION_ID_1, 0L);

		//then
		verify(merchSyncModel, never()).setStatus(any());
		verify(merchSyncModel, never()).setDetails(any());
		verify(merchSyncModel, never()).setNumberOfProducts(any());
		verify(merchSyncModel).setEndTime(any());
		verify(modelService).save(merchSyncModel);
	}

	@Test
	public void testCompleteSyncProcessWhenNullNumberOfProducts()
	{
		//given
		when(merchSynchronizationDao.findByOperationId(OPERATION_ID_1)).thenReturn(Optional.of(merchSyncModel));
		when(merchSyncModel.getStatus()).thenReturn(MerchSyncStatus.STARTED.name());

		//when
		service.completeMerchSyncProcess(OPERATION_ID_1, null);

		//then
		verify(merchSyncModel).setStatus(MerchSyncStatus.COMPLETED.name());
		verify(merchSyncModel).setDetails(MERCH_SYNC_SUCCESSFUL);
		verify(merchSyncModel).setNumberOfProducts(null);
		verify(merchSyncModel).setEndTime(any());
		verify(modelService).save(merchSyncModel);
	}

	@Test
	public void testCompleteSyncProcessWhenNoExist()
	{
		//given
		when(merchSynchronizationDao.findByOperationId(NOT_EXISTING_OPERATION_ID)).thenReturn(Optional.empty());

		//when
		service.completeMerchSyncProcess(NOT_EXISTING_OPERATION_ID, NUMBER_OF_PRODUCTS);

		//then
		verifyNoInteractions(merchSyncModel);
		verifyNoInteractions(modelService);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCompleteSyncProcessWhenNullOperationId()
	{
		//when
		service.completeMerchSyncProcess(null, NUMBER_OF_PRODUCTS);
	}

	@Test
	public void testIsMerchSyncFailed()
	{
		//given
		when(merchSynchronizationDao.findByOperationId(OPERATION_ID_1)).thenReturn(Optional.of(merchSyncModel));
		when(merchSyncModel.getStatus()).thenReturn(MerchSyncStatus.FAILED.name());

		//when
		final boolean result = service.isMerchSyncFailed(OPERATION_ID_1);

		//then
		assertTrue(result);
	}

	@Test
	public void testIsMerchSyncFailedWhenStatusStarted()
	{
		//given
		when(merchSynchronizationDao.findByOperationId(OPERATION_ID_1)).thenReturn(Optional.of(merchSyncModel));
		when(merchSyncModel.getStatus()).thenReturn(MerchSyncStatus.STARTED.name());

		//when
		final boolean result = service.isMerchSyncFailed(OPERATION_ID_1);

		//then
		assertFalse(result);
	}

	@Test
	public void testIsMerchSyncFailedWhenNotExist()
	{
		//given
		when(merchSynchronizationDao.findByOperationId(NOT_EXISTING_OPERATION_ID)).thenReturn(Optional.empty());

		//when
		final boolean result = service.isMerchSyncFailed(NOT_EXISTING_OPERATION_ID);

		//then
		assertFalse(result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsMerchSyncFailedWhenNull()
	{
		//when
		service.isMerchSyncFailed(null);
	}

	@Test
	public void testSaveErrorInfo()
	{
		//given
		final Exception ex = new Exception("Exception");
		final MerchSynchronizationModel merchSync = new MerchSynchronizationModel();
		when(merchSynchronizationDao.findByOperationId(OPERATION_ID_1)).thenReturn(Optional.of(merchSync));

		//when
		service.saveErrorInfo(OPERATION_ID_1, MESSAGE, ex);

		//then
		verify(modelService).save(merchSync);
		assertNotNull(merchSync.getDetails());
		assertTrue(merchSync.getDetails().contains(MESSAGE));
		assertTrue(merchSync.getDetails().contains(ex.getMessage()));
		assertEquals(MerchSyncStatus.FAILED.name(), merchSync.getStatus());
	}

	@Test
	public void testSaveErrorInfoWhenMerchSyncFailed()
	{
		//given
		final Exception ex = new Exception("Exception");
		final MerchSynchronizationModel merchSync = new MerchSynchronizationModel();
		merchSync.setStatus(MerchSyncStatus.FAILED.name());
		merchSync.setDetails(MESSAGE);
		when(merchSynchronizationDao.findByOperationId(OPERATION_ID_1)).thenReturn(Optional.of(merchSync));

		//when
		service.saveErrorInfo(OPERATION_ID_1, "newError", ex);

		//then
		verifyNoInteractions(modelService);
		assertEquals(MESSAGE, merchSync.getDetails());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testErrorInfoWhenOperationIdIsNull()
	{
		//when
		service.saveErrorInfo(null, MESSAGE, new Exception("Exception"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testErrorInfoWhenMessageIsNull()
	{
		//when
		service.saveErrorInfo(OPERATION_ID_1, null, new Exception("Exception"));
	}

	@Test
	public void testErrorInfoWhenExceptionIsNull()
	{
		//given
		final MerchSynchronizationModel merchSync = new MerchSynchronizationModel();
		when(merchSynchronizationDao.findByOperationId(OPERATION_ID_1)).thenReturn(Optional.of(merchSync));

		//when
		service.saveErrorInfo(OPERATION_ID_1, MESSAGE, null);

		//then
		verify(modelService).save(merchSync);
		assertNotNull(merchSync.getDetails());
		assertTrue(merchSync.getDetails().contains(MESSAGE));
		assertEquals(MerchSyncStatus.FAILED.name(), merchSync.getStatus());
	}
}
