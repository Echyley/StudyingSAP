/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.indexer.listeners;

import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchListener;
import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;
import de.hybris.platform.solrfacetsearch.indexer.IndexerListener;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hybris.merchandising.client.MerchCatalogServiceProductDirectoryClient;
import com.hybris.merchandising.exporter.MerchCategoryExporter;
import com.hybris.merchandising.function.FunctionWithExceptions;
import com.hybris.merchandising.model.MerchProductDirectoryConfigModel;
import com.hybris.merchandising.model.Product;
import com.hybris.merchandising.model.ProductPublishContext;
import com.hybris.merchandising.processor.ProductDirectoryProcessor;
import com.hybris.merchandising.service.MerchCatalogService;
import com.hybris.merchandising.service.MerchProductDirectoryConfigService;
import com.hybris.merchandising.service.MerchSyncService;


/**
 * DefaultMerchCatalogListener is a listener which listens to Solr indexing and handles synchronising
 * {@link MerchProductDirectoryConfigModel} definitions to CDS.
 */
public class DefaultMerchCatalogListener implements IndexerBatchListener, IndexerListener
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultMerchCatalogListener.class);
	private static final String LOG_CONTEXT_MERCH_OPERATION_ID = "merchOperationId";
	protected static final String CATALOG_VERSION = "catalogVersion";
	protected static final LoadingCache<String, MerchSyncInfo> merchSyncInfoCache = CacheBuilder.newBuilder()
	                                                                                            .maximumSize(1000)
	                                                                                            .expireAfterAccess(Duration.ofHours(2))
	                                                                                            .build(new CacheLoader<>()
	                                                                                            {
		                                                                                            @Override
		                                                                                            public MerchSyncInfo load(final String key)
		                                                                                            {
			                                                                                            return new MerchSyncInfo();
		                                                                                            }
	                                                                                            });

	private MerchProductDirectoryConfigService merchProductDirectoryConfigService;
	private MerchCatalogService merchCatalogService;
	private MerchCatalogServiceProductDirectoryClient merchCatalogServiceProductDirectoryClient;
	private MerchCategoryExporter merchCategoryExporter;
	private BaseSiteService baseSiteService;
	private ProductDirectoryProcessor productDirectoryProcessor;
	private MerchSyncService merchSyncService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeBatch(final IndexerBatchContext indexerBatchContext)
	{
		//NOOP
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterBatch(final IndexerBatchContext indexerBatchContext)
	{
		executeWithExceptionHandling(getOperationId(indexerBatchContext), "afterBatch", () -> afterBatchInternal(indexerBatchContext));
	}

	protected void afterBatchInternal(final IndexerBatchContext indexerBatchContext) throws IndexerException
	{

		LOG.debug("after batch callback method for index invoked: {} ", indexerBatchContext.getIndexedType().getIdentifier());


		final Optional<MerchProductDirectoryConfigModel> productDirectory = getProductDirectoryConfig(indexerBatchContext.getIndexedType().getIdentifier());

		productDirectory
				.filter(pdc -> StringUtils.isNotBlank(pdc.getCdsIdentifier()) && !isMerchSyncFailed(getOperationId(indexerBatchContext)))
				.map(FunctionWithExceptions
						.rethrowFunction(configModel -> merchCatalogService.getProducts(indexerBatchContext, configModel)))
				.ifPresent(products -> productDirectory.ifPresent(directory -> handleProductsBatch(indexerBatchContext, directory, products)));
	}

	private void handleProductsBatch(final IndexerBatchContext indexerBatchContext, final MerchProductDirectoryConfigModel directory, final List<Product> products)
	{
		LOG.info("Products found to export to Merchandising: {}", products.size());
		//Set the base site to the first one associated with the product directory.
		directory.getBaseSites().stream().findFirst()
		         .ifPresent(site ->
		         {
			         LOG.info("afterBatch - setting current base site to: {}", site.getUid());
			         baseSiteService.setCurrentBaseSite(site, true);
			         if (!products.isEmpty())
			         {
				         if (IndexOperation.FULL == indexerBatchContext.getIndexOperation())
				         {
					         merchCatalogServiceProductDirectoryClient.handleProductsBatch(directory.getCdsIdentifier(),
							         indexerBatchContext.getIndexOperationId(), products);
				         }
				         else
				         {
					         merchCatalogServiceProductDirectoryClient.handleProductsBatch(directory.getCdsIdentifier(),
							         products);
				         }
				         saveBatchInfo(indexerBatchContext, products.size());
			         }
		         });
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterBatchError(final IndexerBatchContext indexerBatchContext)
	{
		//NOOP
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterIndex(final IndexerContext indexerContext)
	{
		executeWithExceptionHandling(getOperationId(indexerContext), "afterIndex", () -> afterIndexInternal(indexerContext));
		executeWithExceptionHandling(getOperationId(indexerContext), "Complete Merch Sync", () -> completeSyncProcess(indexerContext));
	}

	protected void afterIndexInternal(final IndexerContext indexerContext)
	{
		final Optional<MerchProductDirectoryConfigModel> productDirectory = getProductDirectoryConfig(indexerContext.getIndexedType().getIdentifier());

		productDirectory
				.filter(pdc -> StringUtils.isNotBlank(pdc.getCdsIdentifier()) && !isMerchSyncFailed(getOperationId(indexerContext)))
				.ifPresent(directory ->
				{
					if (indexerContext.getIndexOperation().equals(IndexOperation.FULL))
					{
						directory.getBaseSites().stream().findFirst()
						         .ifPresent(site ->
						         {
							         baseSiteService.setCurrentBaseSite(site, true);

							         final String operationId = getOperationId(indexerContext);
							         final Long numberOfProducts = getNumberOfProducts(operationId);
							         LOG.info("Publishing products for product directory : '{}' and full sync version :'{}'. Number of products:{}. EC site : '{}'",
									         directory.getCdsIdentifier(), operationId, numberOfProducts, site.getUid());
							         merchCatalogServiceProductDirectoryClient.publishProducts(directory.getCdsIdentifier(), operationId,
									         new ProductPublishContext(numberOfProducts));

							         LOG.info("Exporting categories for product directory : '{}'. EC site : '{}'", directory.getCdsIdentifier(), site.getUid());
							         merchCategoryExporter.exportCategories(directory);
						         });
					}
				});
	}

	@Override
	public void beforeIndex(final IndexerContext context)
	{
		executeWithExceptionHandling(getOperationId(context), "beforeIndex", () -> beforeIndexInternal(context));
	}

	protected void beforeIndexInternal(final IndexerContext context)
	{
		final Optional<MerchProductDirectoryConfigModel> productDirectory = merchProductDirectoryConfigService
				.getMerchProductDirectoryConfigForIndexedType(context.getIndexedType().getIdentifier());

		productDirectory
				.filter(MerchProductDirectoryConfigModel::isEnabled)
				.ifPresent(configModel ->
				{
					createMerchSync(configModel, context);
					if (StringUtils.isEmpty(configModel.getCdsIdentifier()))
					{
						LOG.info("Create product directory for given indexType: {}", configModel.getIndexedType().getIdentifier());
						productDirectoryProcessor.createUpdate(configModel);
					}
				});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterIndexError(final IndexerContext indexerContext)
	{
		executeWithExceptionHandling(getOperationId(indexerContext), "afterIndexError", () -> afterIndexErrorInternal(indexerContext));
	}

	protected void afterIndexErrorInternal(final IndexerContext indexerContext)
	{
		getProductDirectoryConfig(indexerContext.getIndexedType().getIdentifier())
				.ifPresent(config ->
				{
					final String operationId = getOperationId(indexerContext);
					merchSyncService.saveErrorInfo(operationId, "Merch Sync was not finished because indexing ended with error",
							null);
					merchSyncService.completeMerchSyncProcess(operationId, 0L);
				});
	}

	protected String getOperationId(final IndexerContext indexerContext)
	{
		return String.valueOf(indexerContext.getIndexOperationId());
	}

	private String getOperationId(final IndexerBatchContext indexerBatchContext)
	{
		return String.valueOf(indexerBatchContext.getIndexOperationId());
	}

	protected Optional<MerchProductDirectoryConfigModel> getProductDirectoryConfig(final String indexedTypeId)
	{
		final Optional<MerchProductDirectoryConfigModel> productDirectory = merchProductDirectoryConfigService.getMerchProductDirectoryConfigForIndexedType(
				indexedTypeId);
		return productDirectory.filter(MerchProductDirectoryConfigModel::isEnabled);
	}

	protected void executeWithExceptionHandling(final String operationId, final String step, final Method method)
	{
		setLogContextOperationId(operationId);
		try
		{
			method.execute();
		}
		catch (final Exception e)
		{
			saveErrorInfo(operationId, step, e);
		}
		cleanLogContextOperationId();
	}

	protected void createMerchSync(final MerchProductDirectoryConfigModel config, final IndexerContext context)
	{
		merchSyncService.createMerchSychronization(config, getOperationId(context), context.getIndexOperation().name());
	}

	protected boolean isMerchSyncFailed(final String indexOperationId)
	{
		return merchSyncService.isMerchSyncFailed(indexOperationId);
	}

	protected void completeSyncProcess(final IndexerContext context)
	{
		getProductDirectoryConfig(context.getIndexedType().getIdentifier())
				.ifPresent(config -> {
					final String operationId = getOperationId(context);
					merchSyncService.completeMerchSyncProcess(operationId, getNumberOfProducts(operationId));
				});
	}

	protected void saveBatchInfo(final IndexerBatchContext indexerBatchContext, final int productNumber)
	{
		merchSyncInfoCache.getUnchecked(getOperationId(indexerBatchContext)).addProducts(productNumber);
	}

	protected Long getNumberOfProducts(final String operationId)
	{
		return merchSyncInfoCache.getUnchecked(operationId).getNumberOfProducts();
	}

	protected void saveErrorInfo(final String operationId, final String step, final Exception e)
	{
		try
		{
			merchSyncService.saveErrorInfo(operationId, step, e);
		}
		catch (final Exception exception)
		{
			LOG.error("Error during merchandising synchronization", e);
			LOG.error("Error for merchandising synchronization logging", exception);
		}
	}

	protected void setLogContextOperationId(final String operationId)
	{
		MDC.put(LOG_CONTEXT_MERCH_OPERATION_ID, operationId);
	}

	protected void cleanLogContextOperationId()
	{
		MDC.remove(LOG_CONTEXT_MERCH_OPERATION_ID);
	}

	public void setMerchProductDirectoryConfigService(
			final MerchProductDirectoryConfigService merchProductDirectoryConfigService)
	{
		this.merchProductDirectoryConfigService = merchProductDirectoryConfigService;
	}

	public void setMerchCatalogService(final MerchCatalogService merchCatalogService)
	{
		this.merchCatalogService = merchCatalogService;
	}

	public void setMerchCatalogServiceProductDirectoryClient(
			final MerchCatalogServiceProductDirectoryClient merchCatalogServiceProductDirectoryClient)
	{
		this.merchCatalogServiceProductDirectoryClient = merchCatalogServiceProductDirectoryClient;
	}

	public void setMerchCategoryExporter(final MerchCategoryExporter merchCategoryExporter)
	{
		this.merchCategoryExporter = merchCategoryExporter;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	public void setProductDirectoryProcessor(final ProductDirectoryProcessor productDirectoryProcessor)
	{
		this.productDirectoryProcessor = productDirectoryProcessor;
	}

	protected MerchSyncService getMerchSyncService()
	{
		return merchSyncService;
	}

	public void setMerchSyncService(final MerchSyncService merchSyncService)
	{
		this.merchSyncService = merchSyncService;
	}

	protected interface Method
	{
		void execute() throws Exception;
	}

	protected static class MerchSyncInfo
	{
		private final AtomicLong numberOfProducts = new AtomicLong(0);

		public void addProducts(final long number)
		{
			numberOfProducts.addAndGet(number);
		}

		public long getNumberOfProducts()
		{
			return numberOfProducts.get();
		}
	}
}
