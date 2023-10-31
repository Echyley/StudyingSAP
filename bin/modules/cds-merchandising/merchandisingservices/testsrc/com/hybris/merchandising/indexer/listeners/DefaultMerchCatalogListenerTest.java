/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.indexer.listeners;

import static com.hybris.merchandising.indexer.listeners.DefaultMerchCatalogListener.merchSyncInfoCache;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedTypeModel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.merchandising.client.MerchCatalogServiceProductDirectoryClient;
import com.hybris.merchandising.exceptions.MerchandisingConfigurationException;
import com.hybris.merchandising.exporter.MerchCategoryExporter;
import com.hybris.merchandising.indexer.listeners.DefaultMerchCatalogListener.MerchSyncInfo;
import com.hybris.merchandising.model.MerchProductDirectoryConfigModel;
import com.hybris.merchandising.model.Product;
import com.hybris.merchandising.model.ProductPublishContext;
import com.hybris.merchandising.processor.ProductDirectoryProcessor;
import com.hybris.merchandising.service.MerchCatalogService;
import com.hybris.merchandising.service.MerchProductDirectoryConfigService;
import com.hybris.merchandising.service.MerchSyncService;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultMerchCatalogListenerTest
{
	private static final String BATCH_INDEXER_ID = "batch001";
	private static final String PRODUCT_DIRECTORY_ID = "prod1";
	private static final Long INDEX_OPERATION_ID = 1L;
	private static final String OPERATION_ID = "1";

	@Mock
	private MerchProductDirectoryConfigService merchProductDirectoryConfigService;

	@Mock
	private MerchCatalogService merchCatalogService;

	@Mock
	private ProductDirectoryProcessor preIndexProcessor;

	@Mock
	private IndexerBatchContext indexerBatchContext;

	@Mock
	private IndexerContext indexerContext;

	@Mock
	private MerchProductDirectoryConfigModel merchProductDirectoryConfigModel;

	@Mock
	private MerchCatalogServiceProductDirectoryClient productDirectoryClient;

	@Mock
	private MerchCategoryExporter categoryExporter;

	@Mock
	private BaseSiteService mockBaseSiteService;

	@Mock
	private BaseSiteModel mockBaseSite;

	@Mock
	private Product mockProduct;

	@Mock
	private SolrIndexedTypeModel solrIndexedTypeModel;

	@Mock
	private MerchSyncService merchSyncService;

	@InjectMocks
	private DefaultMerchCatalogListener defaultMerchCatalogListener;

	private List<Product> products;
	private final Exception merchConfigException = new MerchandisingConfigurationException("config error");

	@Before
	public void setUp() throws IndexerException
	{

		when(merchProductDirectoryConfigModel.getCdsIdentifier()).thenReturn(PRODUCT_DIRECTORY_ID);
		products = Collections.singletonList(mockProduct);
		when(merchProductDirectoryConfigService.getMerchProductDirectoryConfigForIndexedType(ArgumentMatchers.anyString()))
				.thenReturn(Optional.of(merchProductDirectoryConfigModel));

		when(merchCatalogService.getProducts(indexerBatchContext, merchProductDirectoryConfigModel))
				.thenReturn(products);
		when(merchProductDirectoryConfigModel.isEnabled()).thenReturn(Boolean.TRUE);
		final IndexedType type = Mockito.mock(IndexedType.class);
		when(indexerBatchContext.getIndexOperationId()).thenReturn(INDEX_OPERATION_ID);
		when(indexerBatchContext.getIndexedType()).thenReturn(type);
		when(type.getIdentifier()).thenReturn(BATCH_INDEXER_ID);
		when(indexerContext.getIndexedType()).thenReturn(type);

		when(indexerContext.getIndexOperation()).thenReturn(IndexOperation.FULL);
		when(indexerContext.getIndexOperationId()).thenReturn(INDEX_OPERATION_ID);
		when(merchProductDirectoryConfigModel.getIndexedType()).thenReturn(solrIndexedTypeModel);
		when(solrIndexedTypeModel.getIdentifier()).thenReturn("indexTypeId");

		mockBaseSite = Mockito.mock(BaseSiteModel.class);
		when(mockBaseSite.getUid()).thenReturn("apparel-uk");
		when(merchProductDirectoryConfigModel.getBaseSites()).thenReturn(Collections.singletonList(mockBaseSite));

		when(merchSyncService.isMerchSyncFailed(OPERATION_ID)).thenReturn(Boolean.FALSE);

		merchSyncInfoCache.invalidate(OPERATION_ID);
	}

	@Test
	public void testAfterBatchFull() throws IndexerException
	{
		//given
		when(indexerBatchContext.getIndexOperation()).thenReturn(IndexOperation.FULL);

		//when
		defaultMerchCatalogListener.afterBatch(indexerBatchContext);

		//then
		verifyBatchSynchronized(true);
	}

	@Test
	public void testAfterBatchPartial() throws IndexerException
	{   //given
		when(indexerBatchContext.getIndexOperation()).thenReturn(IndexOperation.PARTIAL_UPDATE);

		//when
		defaultMerchCatalogListener.afterBatch(indexerBatchContext);

		//then
		verifyBatchSynchronized(false);
	}

	private void verifyBatchSynchronized(final boolean fullSync) throws IndexerException
	{
		verify(merchProductDirectoryConfigService, times(1)).getMerchProductDirectoryConfigForIndexedType(ArgumentMatchers.anyString());
		verify(merchCatalogService).getProducts(indexerBatchContext, merchProductDirectoryConfigModel);
		verify(mockBaseSiteService).setCurrentBaseSite(mockBaseSite, Boolean.TRUE);
		final MerchSyncInfo merchSyncInfo = merchSyncInfoCache.getIfPresent(OPERATION_ID);
		assertNotNull(merchSyncInfo);
		assertEquals(products.size(), merchSyncInfo.getNumberOfProducts());

		if (fullSync)
		{
			verify(productDirectoryClient).handleProductsBatch(PRODUCT_DIRECTORY_ID, INDEX_OPERATION_ID, products);
		}
		else
		{
			verify(productDirectoryClient).handleProductsBatch(PRODUCT_DIRECTORY_ID, products);
		}
	}

	@Test
	public void testAfterBatchWithNullProductDirectoryId() throws IndexerException
	{   //given
		when(merchProductDirectoryConfigModel.getCdsIdentifier()).thenReturn(null);

		//when
		defaultMerchCatalogListener.afterBatch(indexerBatchContext);

		//then
		verifyBatchNotSynchronized();
	}

	@Test
	public void testAfterBatchWithDisabledMerchConfig() throws IndexerException
	{   //given
		when(merchProductDirectoryConfigModel.isEnabled()).thenReturn(Boolean.FALSE);

		//when
		defaultMerchCatalogListener.afterBatch(indexerBatchContext);

		//then
		verifyBatchNotSynchronized();
	}

	private void verifyBatchNotSynchronized() throws IndexerException
	{
		verify(merchProductDirectoryConfigService).getMerchProductDirectoryConfigForIndexedType(anyString());
		verify(merchCatalogService, never()).getProducts(indexerBatchContext, merchProductDirectoryConfigModel);
		verify(productDirectoryClient, never()).handleProductsBatch(PRODUCT_DIRECTORY_ID, products);
		assertNull(merchSyncInfoCache.getIfPresent(OPERATION_ID));
	}

	@Test
	public void testAfterBatchWhenError() throws IndexerException
	{   //given
		when(indexerBatchContext.getIndexOperation()).thenReturn(IndexOperation.FULL);
		when(productDirectoryClient.handleProductsBatch(PRODUCT_DIRECTORY_ID, INDEX_OPERATION_ID, products)).thenThrow(merchConfigException);

		//when
		defaultMerchCatalogListener.afterBatch(indexerBatchContext);

		//then
		assertNull(merchSyncInfoCache.getIfPresent(OPERATION_ID));
		verify(merchSyncService).saveErrorInfo(eq(OPERATION_ID), anyString(), eq(merchConfigException));
	}


	@Test
	public void testAfterIndexFull() throws IndexerException
	{
		//given
		when(indexerContext.getIndexOperation()).thenReturn(IndexOperation.FULL);
		when(indexerContext.getIndexOperationId()).thenReturn(INDEX_OPERATION_ID);

		//when
		defaultMerchCatalogListener.afterIndex(indexerContext);

		//then
		verify(mockBaseSiteService).setCurrentBaseSite(mockBaseSite, Boolean.TRUE);
		verify(productDirectoryClient).publishProducts(eq(PRODUCT_DIRECTORY_ID), eq(OPERATION_ID), any(ProductPublishContext.class));
		verify(categoryExporter).exportCategories(merchProductDirectoryConfigModel);
		verify(merchSyncService).completeMerchSyncProcess(OPERATION_ID, 0L);
	}

	@Test
	public void testAfterIndexPartial() throws IndexerException
	{
		//given
		when(indexerContext.getIndexOperation()).thenReturn(IndexOperation.PARTIAL_UPDATE);
		final MerchSyncInfo info = new MerchSyncInfo();
		info.addProducts(10);
		merchSyncInfoCache.put(OPERATION_ID, info);

		//when
		defaultMerchCatalogListener.afterIndex(indexerContext);

		//then
		verify(productDirectoryClient, never()).publishProducts(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());
		verify(categoryExporter, never()).exportCategories(merchProductDirectoryConfigModel);
		verify(merchSyncService).completeMerchSyncProcess(OPERATION_ID, 10L);
	}

	@Test
	public void testAfterIndexWithNullProductDirectoryId() throws IndexerException
	{
		//given
		when(merchProductDirectoryConfigModel.getCdsIdentifier()).thenReturn(null);

		//when
		defaultMerchCatalogListener.afterIndex(indexerContext);

		//then
		verifySyncNotExecuted();
		verify(merchSyncService).completeMerchSyncProcess(eq(OPERATION_ID), any());
	}

	@Test
	public void testAfterIndexWithDisabledMerchConfig() throws IndexerException
	{
		//given
		when(merchProductDirectoryConfigModel.isEnabled()).thenReturn(Boolean.FALSE);

		//when
		defaultMerchCatalogListener.afterIndex(indexerContext);

		//then
		verifySyncNotExecuted();
		verify(merchSyncService, never()).completeMerchSyncProcess(eq(OPERATION_ID), any());
	}

	private void verifySyncNotExecuted()
	{
		verify(productDirectoryClient, never()).publishProducts(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());
		verify(categoryExporter, never()).exportCategories(merchProductDirectoryConfigModel);
	}

	@Test
	public void testAfterIndexWhenError() throws IndexerException
	{
		//given
		final Exception error = new MerchandisingConfigurationException("sync error");
		when(productDirectoryClient.publishProducts(eq(PRODUCT_DIRECTORY_ID), eq(OPERATION_ID), any(ProductPublishContext.class))).thenThrow(error);


		//when
		defaultMerchCatalogListener.afterIndex(indexerContext);

		//then
		verify(productDirectoryClient).publishProducts(eq(PRODUCT_DIRECTORY_ID), eq(OPERATION_ID), any(ProductPublishContext.class));
		verify(categoryExporter, never()).exportCategories(merchProductDirectoryConfigModel);
		verify(merchSyncService).saveErrorInfo(eq(OPERATION_ID), anyString(), eq(error));
		verify(merchSyncService).completeMerchSyncProcess(OPERATION_ID, 0L);
	}

	@Test
	public void testBeforeIndexWithProductDirectoryId() throws IndexerException
	{
		//when
		defaultMerchCatalogListener.beforeIndex(indexerContext);

		//then
		verify(merchProductDirectoryConfigService, times(1)).getMerchProductDirectoryConfigForIndexedType(ArgumentMatchers.anyString());
		verify(preIndexProcessor, never()).createUpdate(merchProductDirectoryConfigModel);
		verify(merchSyncService).createMerchSychronization(merchProductDirectoryConfigModel, OPERATION_ID, indexerContext.getIndexOperation().name());
	}

	@Test
	public void testBeforeIndexWithNullProductDirectoryId()
	{
		//given
		when(merchProductDirectoryConfigModel.getCdsIdentifier()).thenReturn(null);

		//when
		defaultMerchCatalogListener.beforeIndex(indexerContext);

		//then
		verify(merchProductDirectoryConfigService, times(1)).getMerchProductDirectoryConfigForIndexedType(ArgumentMatchers.anyString());
		verify(preIndexProcessor, times(1)).createUpdate(merchProductDirectoryConfigModel);
		verify(merchSyncService).createMerchSychronization(merchProductDirectoryConfigModel, OPERATION_ID, indexerContext.getIndexOperation().name());
	}

	@Test
	public void testBeforeIndexWithDisabledProductDirectoryId() throws IndexerException
	{
		//given
		when(merchProductDirectoryConfigModel.isEnabled()).thenReturn(Boolean.FALSE);

		//when
		defaultMerchCatalogListener.beforeIndex(indexerContext);

		//then
		verify(merchProductDirectoryConfigService, times(1)).getMerchProductDirectoryConfigForIndexedType(ArgumentMatchers.anyString());
		verify(preIndexProcessor, never()).createUpdate(merchProductDirectoryConfigModel);
		verify(merchSyncService, never()).createMerchSychronization(merchProductDirectoryConfigModel, OPERATION_ID, indexerContext.getIndexOperation().name());

	}

	@Test
	public void testAfterIndexError() throws IndexerException
	{
		//when
		defaultMerchCatalogListener.afterIndexError(indexerContext);

		//then
		verify(merchProductDirectoryConfigService).getMerchProductDirectoryConfigForIndexedType(anyString());
		verify(merchSyncService).saveErrorInfo(eq(OPERATION_ID), anyString(), eq(null));
		verify(merchSyncService).completeMerchSyncProcess(OPERATION_ID, 0L);
	}

	@Test
	public void testAfterIndexErrorWithDisabledMerchConfig() throws IndexerException
	{
		//given
		when(merchProductDirectoryConfigModel.isEnabled()).thenReturn(Boolean.FALSE);

		//when
		defaultMerchCatalogListener.afterIndexError(indexerContext);

		//then
		verify(merchProductDirectoryConfigService).getMerchProductDirectoryConfigForIndexedType(anyString());
		verify(merchSyncService, never()).saveErrorInfo(eq(OPERATION_ID), anyString(), eq(null));
		verify(merchSyncService, never()).completeMerchSyncProcess(eq(OPERATION_ID), any());
	}

}
