/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.indexer.listeners;

import static com.hybris.merchandising.indexer.listeners.DefaultMerchSnIndexerListener.merchContextCache;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.searchservices.admin.data.SnIndexType;
import de.hybris.platform.searchservices.enums.SnIndexerOperationType;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchContext;
import de.hybris.platform.searchservices.model.SnIndexConfigurationModel;
import de.hybris.platform.searchservices.model.SnIndexTypeModel;
import de.hybris.platform.searchservices.search.data.SnSearchQuery;
import de.hybris.platform.searchservices.search.data.SnSearchResult;
import de.hybris.platform.searchservices.search.service.SnSearchRequest;
import de.hybris.platform.searchservices.search.service.SnSearchResponse;
import de.hybris.platform.searchservices.search.service.SnSearchService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.merchandising.client.MerchCatalogServiceProductDirectoryClient;
import com.hybris.merchandising.exceptions.MerchandisingConfigurationException;
import com.hybris.merchandising.model.MerchSnConfigModel;
import com.hybris.merchandising.model.MerchSnSynchContext;
import com.hybris.merchandising.model.Product;
import com.hybris.merchandising.service.MerchCatalogService;
import com.hybris.merchandising.service.MerchSnConfigService;
import com.hybris.merchandising.service.MerchSyncService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMerchSnIndexerListenerTest
{
	protected static String INDEX_TYPE_ID = "electronics-product";
	protected static String OPERATION_ID = "operationId";
	@Mock
	MerchSnConfigService merchSnConfigService;
	@Mock
	MerchCatalogService merchCatalogService;
	@Mock
	MerchCatalogServiceProductDirectoryClient merchCatalogServiceProductDirectoryClient;
	@Mock
	MerchSyncService merchSyncService;
	@Mock
	ImpersonationService impersonationService;
	@Mock
	SnSearchService snSearchService;
	@Mock
	CommonI18NService commonI18NService;

	@Mock
	MerchSnConfigModel merchConfig;
	@Mock
	SnIndexerBatchContext indexerContext;
	@Mock
	SnIndexType indexType;
	@Mock
	SnIndexTypeModel indexTypeModel;
	@Mock
	SnIndexConfigurationModel indexConfModel;
	@Mock
	LanguageModel defaultLanguage;
	@Mock
	SnSearchResponse searchResponse;
	@Mock
	SnSearchResult searchResult;
	@Mock
	SnSearchRequest searchRequest;
	@Mock
	SnSearchQuery searchQuery;
	@Mock
	private Product mockProduct;

	@InjectMocks
	private DefaultMerchSnIndexerListener listener;

	@Before
	public void setUp()
	{

		when(indexerContext.getIndexerOperationId()).thenReturn(OPERATION_ID);
		when(indexerContext.getIndexType()).thenReturn(indexType);
		when(indexType.getId()).thenReturn(INDEX_TYPE_ID);

		when(merchSnConfigService.getMerchConfigForIndexedType(INDEX_TYPE_ID)).thenReturn(Optional.of(merchConfig));
		when(merchConfig.isEnabled()).thenReturn(true);
		when(merchConfig.getCdsIdentifier()).thenReturn("prodDir1");

		when(merchSyncService.isMerchSyncFailed(OPERATION_ID)).thenReturn(Boolean.FALSE);

		merchContextCache.invalidate(OPERATION_ID);
	}

	@Test
	public void testBeforeIndex() throws Throwable
	{
		//given
		when(merchSyncService.getMerchSynchronization(OPERATION_ID)).thenReturn(Optional.empty());
		when(indexerContext.getIndexerOperationType()).thenReturn(SnIndexerOperationType.INCREMENTAL);
		mockMerchContextCreation();

		//when
		listener.beforeIndex(indexerContext);

		//then
		verify(merchSyncService).createMerchSychronization(merchConfig, OPERATION_ID, SnIndexerOperationType.INCREMENTAL.name());
		assertNotNull(merchContextCache.getIfPresent(OPERATION_ID));
		verify(merchSnConfigService, never()).createOrUpdateProductDirectory(merchConfig, true);
	}

	@Test
	public void testBeforeIndexWhenProdDirIsNull() throws Throwable
	{
		//given
		when(merchSyncService.getMerchSynchronization(OPERATION_ID)).thenReturn(Optional.empty());
		when(indexerContext.getIndexerOperationType()).thenReturn(SnIndexerOperationType.FULL);
		when(merchConfig.getCdsIdentifier()).thenReturn(null);
		mockMerchContextCreation();


		//when
		listener.beforeIndex(indexerContext);

		//then
		verify(merchSyncService).createMerchSychronization(merchConfig, OPERATION_ID, SnIndexerOperationType.FULL.name());
		assertNotNull(merchContextCache.getIfPresent(OPERATION_ID));
		verify(merchSnConfigService, times(1)).createOrUpdateProductDirectory(merchConfig, true);
	}

	@Test
	public void testBeforeIndexWhenConfigDisabled()
	{
		//given
		when(merchConfig.isEnabled()).thenReturn(false);

		//when
		listener.beforeIndexInternal(indexerContext);

		//then
		verifyNoInteractions(merchSyncService);
		assertNull(merchContextCache.getIfPresent(OPERATION_ID));
		verify(merchSnConfigService, never()).createOrUpdateProductDirectory(merchConfig, true);
	}

	@Test
	public void testAfterIndexFull()
	{
		//given
		when(indexerContext.getIndexerOperationType()).thenReturn(SnIndexerOperationType.FULL);

		//when
		listener.afterIndex(indexerContext);

		//then
		verify(impersonationService).executeInContext(any(), any());
		verify(merchSyncService).completeMerchSyncProcess(OPERATION_ID, 0L);
	}

	@Test
	public void testAfterIndexFullWhenProdDirIsNull()
	{
		//given
		when(merchConfig.getCdsIdentifier()).thenReturn(null);

		//when
		listener.afterIndex(indexerContext);

		//then
		verifyNoInteractions(impersonationService);
		verify(merchSyncService).completeMerchSyncProcess(eq(OPERATION_ID), any());
	}

	@Test
	public void testAfterIndexIncremental()
	{
		//given
		when(indexerContext.getIndexerOperationType()).thenReturn(SnIndexerOperationType.INCREMENTAL);

		//when
		listener.afterIndex(indexerContext);

		//then
		verifyNoInteractions(impersonationService);
		verify(merchSyncService).completeMerchSyncProcess(OPERATION_ID, 0L);
	}

	@Test
	public void testAfterIndexWhenMerchConfigDisabled()
	{
		//given
		when(merchConfig.isEnabled()).thenReturn(false);

		//when
		listener.afterIndex(indexerContext);

		//then
		verifyNoInteractions(impersonationService);
		verify(merchSyncService, never()).completeMerchSyncProcess(eq(OPERATION_ID), any());
	}

	@Test
	public void testAfterIndexWhenError() throws Throwable
	{
		//given
		when(indexerContext.getIndexerOperationType()).thenReturn(SnIndexerOperationType.FULL);
		final Exception error = new MerchandisingConfigurationException("sync error");
		when(impersonationService.executeInContext(any(), any())).thenThrow(error);


		//when
		listener.afterIndex(indexerContext);

		//then
		verify(merchSyncService).saveErrorInfo(eq(OPERATION_ID), anyString(), eq(error));
		verify(merchSyncService).completeMerchSyncProcess(OPERATION_ID, 0L);
	}

	@Test
	public void testAfterBatch()
	{
		//given
		final MerchSnSynchContext context = new MerchSnSynchContext();
		merchContextCache.put(OPERATION_ID, context);
		when(merchCatalogService.getProducts(indexerContext, merchConfig, context)).thenReturn(List.of(mockProduct));

		//when
		listener.afterIndexBatch(indexerContext);

		//then
		verify(impersonationService).executeInContext(any(), any());
		assertEquals(1, context.getNumberOfProducts());
	}

	@Test
	public void testAfterBatchWhenProdDirIsNull()
	{
		//given
		when(merchConfig.getCdsIdentifier()).thenReturn(null);
		final MerchSnSynchContext context = new MerchSnSynchContext();
		merchContextCache.put(OPERATION_ID, context);

		//when
		listener.afterIndexBatch(indexerContext);

		//then
		verifyNoInteractions(impersonationService);
		assertEquals(0, context.getNumberOfProducts());
	}

	@Test
	public void testAfterBatchWhenMerchConfigDisabled()
	{
		//given
		when(merchConfig.isEnabled()).thenReturn(false);
		final MerchSnSynchContext context = new MerchSnSynchContext();
		merchContextCache.put(OPERATION_ID, context);

		//when
		listener.afterIndexBatch(indexerContext);

		//then
		verifyNoInteractions(impersonationService);
		assertEquals(0, context.getNumberOfProducts());
	}

	@Test
	public void testAfterBatchWhenError() throws Throwable
	{   //given
		final MerchSnSynchContext context = new MerchSnSynchContext();
		merchContextCache.put(OPERATION_ID, context);
		when(merchCatalogService.getProducts(indexerContext, merchConfig, context)).thenReturn(List.of(mockProduct));
		final Exception error = new MerchandisingConfigurationException("sync error");
		when(impersonationService.executeInContext(any(), any())).thenThrow(error);

		//when
		listener.afterIndexBatch(indexerContext);

		//then
		verify(merchSyncService).saveErrorInfo(eq(OPERATION_ID), anyString(), eq(error));
	}

	@Test
	public void testAfterIndexError()
	{
		//when
		listener.afterIndexError(indexerContext);

		//then
		verify(merchSyncService).saveErrorInfo(eq(OPERATION_ID), anyString(), eq(null));
		verify(merchSyncService).completeMerchSyncProcess(OPERATION_ID, 0L);
	}

	@Test
	public void testAfterIndexErrorWithDisabledMerchConfig()
	{
		//given
		when(merchConfig.isEnabled()).thenReturn(Boolean.FALSE);

		//when
		listener.afterIndexError(indexerContext);

		//then
		verify(merchSyncService, never()).saveErrorInfo(eq(OPERATION_ID), anyString(), eq(null));
		verify(merchSyncService, never()).completeMerchSyncProcess(eq(OPERATION_ID), any());
	}

	private void mockMerchContextCreation() throws Throwable
	{
		when(merchConfig.getMerchSnFields()).thenReturn(List.of());
		when(merchConfig.getDefaultLanguage()).thenReturn(defaultLanguage);
		when(merchConfig.getSnIndexType()).thenReturn(indexTypeModel);
		when(indexTypeModel.getIndexConfiguration()).thenReturn(indexConfModel);
		when(indexConfModel.getLanguages()).thenReturn(List.of(defaultLanguage));
		when(commonI18NService.getLocaleForLanguage(defaultLanguage)).thenReturn(Locale.US);
		mockSearchQueryExecution();
	}

	private void mockSearchQueryExecution() throws Throwable
	{
		when(impersonationService.executeInContext(any(), any())).thenReturn(searchResponse);
		when(searchResponse.getSearchResult()).thenReturn(searchResult);
		when(searchResult.getFacets()).thenReturn(List.of());
		when(snSearchService.createSearchRequest(eq(INDEX_TYPE_ID), any())).thenReturn(searchRequest);
		when(searchRequest.getSearchQuery()).thenReturn(searchQuery);
		when(searchQuery.getFacets()).thenReturn(List.of());
	}
}
