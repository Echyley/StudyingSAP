/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.indexer.listeners;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.searchservices.enums.SnIndexerOperationType;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchListener;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerListener;
import de.hybris.platform.searchservices.search.SnSearchException;
import de.hybris.platform.searchservices.search.data.AbstractSnFacetRequest;
import de.hybris.platform.searchservices.search.data.AbstractSnFacetResponse;
import de.hybris.platform.searchservices.search.data.SnSearchQuery;
import de.hybris.platform.searchservices.search.service.SnSearchRequest;
import de.hybris.platform.searchservices.search.service.SnSearchResponse;
import de.hybris.platform.searchservices.search.service.SnSearchService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hybris.merchandising.client.CategoryHierarchyWrapper;
import com.hybris.merchandising.client.MerchCatalogServiceProductDirectoryClient;
import com.hybris.merchandising.exceptions.MerchSynchException;
import com.hybris.merchandising.model.MerchSnConfigModel;
import com.hybris.merchandising.model.MerchSnFieldModel;
import com.hybris.merchandising.model.MerchSnSynchContext;
import com.hybris.merchandising.model.Product;
import com.hybris.merchandising.model.ProductPublishContext;
import com.hybris.merchandising.service.MerchCatalogService;
import com.hybris.merchandising.service.MerchSnConfigService;
import com.hybris.merchandising.service.MerchSyncService;
import com.hybris.platform.merchandising.yaas.CategoryHierarchy;


/**
 * Listener which listens to search service indexing and handles synchronising products to catalog service (CDS/ISS).
 */
public class DefaultMerchSnIndexerListener implements SnIndexerBatchListener, SnIndexerListener
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultMerchSnIndexerListener.class);
	protected static final String LOG_CONTEXT_MERCH_OPERATION_ID = "merchOperationId";
	protected static final Cache<String, MerchSnSynchContext> merchContextCache = CacheBuilder.newBuilder()
	                                                                                          .maximumSize(1000)
	                                                                                          .expireAfterAccess(Duration.ofHours(3))
	                                                                                          .build();

	private MerchSnConfigService merchSnConfigService;
	private MerchCatalogService merchCatalogService;
	private MerchCatalogServiceProductDirectoryClient merchCatalogServiceProductDirectoryClient;
	private MerchSyncService merchSyncService;
	private ImpersonationService impersonationService;
	private SnSearchService snSearchService;
	private CommonI18NService commonI18NService;

	@Override
	public void beforeIndex(final SnIndexerContext indexerContext)
	{
		executeWithExceptionHandling(getOperationId(indexerContext), "beforeIndex", () -> beforeIndexInternal(indexerContext));
	}

	protected void beforeIndexInternal(final SnIndexerContext indexerContext)
	{
		getProductDirectoryConfig(indexerContext.getIndexType().getId())
				.ifPresent(pdc ->
				{
					createMerchSync(pdc, indexerContext);
					createMerchContext(indexerContext);
					if (StringUtils.isEmpty(pdc.getCdsIdentifier()))
					{
						LOG.info("Create product directory for given indexType: {}", pdc.getSnIndexType().getId());
						merchSnConfigService.createOrUpdateProductDirectory(pdc, true);
					}
				});
	}

	@Override
	public void afterIndex(final SnIndexerContext indexerContext)
	{
		executeWithExceptionHandling(getOperationId(indexerContext), "afterIndex", () -> afterIndexInternal(indexerContext));
		executeWithExceptionHandling(getOperationId(indexerContext), "Complete Merch Sync", () -> completeSyncProcess(indexerContext));
	}

	protected void afterIndexInternal(final SnIndexerContext indexerContext)
	{
		final Optional<MerchSnConfigModel> prodDirConfig = getProductDirectoryConfig(indexerContext.getIndexType().getId());
		final String operationId = getOperationId(indexerContext);

		prodDirConfig.filter(pdc -> StringUtils.isNotBlank(pdc.getCdsIdentifier()) && !isMerchSyncFailed(operationId))
		             .ifPresent(pdc ->
		             {
			             if (SnIndexerOperationType.FULL.equals(indexerContext.getIndexerOperationType()))
			             {
				             final ImpersonationContext runContext = new ImpersonationContext();
				             final BaseSiteModel site = pdc.getBaseSite();
				             runContext.setSite(site);
				             impersonationService.executeInContext(runContext, () -> {
					             final Long numberOfProducts = getNumberOfProducts(operationId);
					             LOG.info("Publishing products for product directory : '{}' and full sync version :'{}'. Number of products:{}. EC site : '{}'",
							             pdc.getCdsIdentifier(), operationId, numberOfProducts, site.getUid());
					             merchCatalogServiceProductDirectoryClient.publishProducts(pdc.getCdsIdentifier(), operationId,
							             new ProductPublishContext(numberOfProducts));

					             LOG.info("Exporting categories for product directory : '{}'. EC site : '{}'", pdc.getCdsIdentifier(), site.getUid());
					             final List<CategoryHierarchy> categoryHierarchy = merchCatalogService.getCategories(pdc);
					             merchCatalogServiceProductDirectoryClient.handleCategories(pdc.getCdsIdentifier(), new CategoryHierarchyWrapper(categoryHierarchy));

					             return operationId;
				             });
			             }
		             });
	}

	@Override
	public void afterIndexError(final SnIndexerContext indexerContext)
	{
		executeWithExceptionHandling(getOperationId(indexerContext), "afterIndexError", () -> afterIndexErrorInternal(indexerContext));
	}

	protected void afterIndexErrorInternal(final SnIndexerContext indexerContext)
	{
		getProductDirectoryConfig(indexerContext.getIndexType().getId())
				.ifPresent(config ->
				{
					final String operationId = getOperationId(indexerContext);
					merchSyncService.saveErrorInfo(operationId, "Merch Sync was not finished because indexing ended with error", null);
					merchSyncService.completeMerchSyncProcess(operationId, 0L);
					invalidateMerchContext(indexerContext);
				});
	}

	@Override
	public void beforeIndexBatch(final SnIndexerBatchContext indexerContext)
	{
		//NOOP
	}

	@Override
	public void afterIndexBatch(final SnIndexerBatchContext indexerBatchContext)
	{
		executeWithExceptionHandling(getOperationId(indexerBatchContext), "afterBatch", () -> afterBatchInternal(indexerBatchContext));
	}

	protected void afterBatchInternal(final SnIndexerBatchContext indexerBatchContext)
	{
		LOG.debug("after batch callback method for index invoked: {} ", indexerBatchContext.getIndexType().getId());
		final Optional<MerchSnConfigModel> prodDirConfig = getProductDirectoryConfig(indexerBatchContext.getIndexType().getId());
		final String operationId = getOperationId(indexerBatchContext);

		if (prodDirConfig.isPresent() && StringUtils.isNotBlank(prodDirConfig.get().getCdsIdentifier()) && !isMerchSyncFailed(operationId))
		{
			final MerchSnConfigModel pdc = prodDirConfig.get();
			final MerchSnSynchContext merchContext = getMerchContext(indexerBatchContext);

			final List<Product> products = merchCatalogService.getProducts(indexerBatchContext, pdc, merchContext);
			LOG.info("Products found to export to Merchandising: {}", products.size());

			if (!products.isEmpty())
			{
				final ImpersonationContext runContext = new ImpersonationContext();
				final BaseSiteModel site = pdc.getBaseSite();
				runContext.setSite(site);
				impersonationService.executeInContext(runContext, () -> {
					if (SnIndexerOperationType.FULL == indexerBatchContext.getIndexerOperationType())
					{
						merchCatalogServiceProductDirectoryClient.handleProductsBatch(pdc.getCdsIdentifier(), operationId, products);
					}
					else
					{
						merchCatalogServiceProductDirectoryClient.handleProductsBatch(pdc.getCdsIdentifier(), products);
					}
					return products.size();
				});
				saveBatchInfo(merchContext, products.size());
			}
		}
	}


	@Override
	public void afterIndexBatchError(final SnIndexerBatchContext indexerBatchContext)
	{
		//NOOP
	}

	protected String getOperationId(final SnIndexerContext indexerContext)
	{
		return indexerContext.getIndexerOperationId();
	}

	protected Optional<MerchSnConfigModel> getProductDirectoryConfig(final String indexedType)
	{
		final Optional<MerchSnConfigModel> productDirectory = merchSnConfigService.getMerchConfigForIndexedType(indexedType);
		return productDirectory.filter(MerchSnConfigModel::isEnabled);
	}

	protected MerchSnSynchContext getMerchContext(final SnIndexerContext indexerContext)
	{
		final String operationId = getOperationId(indexerContext);
		MerchSnSynchContext context = operationId == null ? null : merchContextCache.getIfPresent(operationId);
		if (context == null)
		{
			context = createMerchContext(indexerContext);
		}

		return context;
	}

	protected MerchSnSynchContext createMerchContext(final SnIndexerContext indexerContext)
	{
		final Optional<MerchSnConfigModel> prodDirConfig = getProductDirectoryConfig(indexerContext.getIndexType().getId());

		return prodDirConfig.map(config -> {
			final Map<String, String> merchSnFieldsMapping = createMerchSnFieldsMapping(config.getMerchSnFields());
			final MerchSnSynchContext context = MerchSnSynchContext.from(merchSnFieldsMapping, getDefaultLocale(config),
					createLocaleMap(config));
			setMerchFacetMapping(context, indexerContext, config);

			final String operationId = getOperationId(indexerContext);
			if (operationId != null)
			{
				merchContextCache.put(operationId, context);
			}
			return context;

		}).orElse(null);

	}

	protected void invalidateMerchContext(final SnIndexerContext indexerContext)
	{
		final String operationId = getOperationId(indexerContext);
		if (operationId != null)
		{
			merchContextCache.invalidate(operationId);
		}
	}

	protected Map<String, String> createMerchSnFieldsMapping(final List<MerchSnFieldModel> merchSnFields)
	{
		return merchSnFields.stream()
		                    .filter(merchSnField -> Objects.nonNull(merchSnField.getIndexedField()))
		                    .collect(Collectors.toMap(this::getMerchMappedName, this::getFieldId));
	}

	private String getMerchMappedName(final MerchSnFieldModel field)
	{
		return StringUtils.isNotBlank(field.getMerchMappedName()) ? field.getMerchMappedName() : getFieldId(field);
	}

	private String getFieldId(final MerchSnFieldModel field)
	{
		return field.getIndexedField().getId();
	}

	protected Locale getDefaultLocale(final MerchSnConfigModel pdc)
	{
		return commonI18NService.getLocaleForLanguage(pdc.getDefaultLanguage());
	}

	protected Map<LanguageModel, Locale> createLocaleMap(final MerchSnConfigModel pdc)
	{
		return pdc.getSnIndexType().getIndexConfiguration().getLanguages().stream()
		          .collect(Collectors.toMap(Function.identity(), lang -> commonI18NService.getLocaleForLanguage(lang)));
	}

	private SnSearchQuery createSearchQuery()
	{
		final SnSearchQuery query = new SnSearchQuery();
		query.setQueryContexts(List.of("DEFAULT"));
		query.setOffset(0);
		query.setLimit(1);
		return query;
	}

	protected void setMerchFacetMapping(final MerchSnSynchContext merchContext, final SnIndexerContext context, final MerchSnConfigModel pdc) throws MerchSynchException
	{
		try
		{
			final ImpersonationContext runContext = new ImpersonationContext();
			final BaseSiteModel site = pdc.getBaseSite();
			runContext.setSite(site);
			runContext.setCatalogVersions(merchCatalogService.getCatalogVersionsToSynch(pdc));
			runContext.setCurrency(pdc.getCurrency());
			runContext.setLanguage(pdc.getDefaultLanguage());

			final SnSearchQuery query = createSearchQuery();
			final SnSearchRequest request = snSearchService.createSearchRequest(context.getIndexType().getId(), query);
			final SnSearchResponse response = impersonationService.executeInContext(runContext, () -> snSearchService.search(request));

			merchContext.setFacetResponses(response.getSearchResult().getFacets().stream()
			                                       .collect(Collectors.toMap(AbstractSnFacetResponse::getId, Function.identity())));
			merchContext.setFacetRequests(request.getSearchQuery().getFacets().stream()
			                                     .collect(Collectors.toMap(AbstractSnFacetRequest::getId, Function.identity())));

		}
		catch (final SnSearchException e)
		{
			throw new MerchSynchException("Error during creating facet list", e);
		}
	}

	protected void createMerchSync(final MerchSnConfigModel config, final SnIndexerContext context)
	{
		final String operationId = getOperationId(context);
		if (merchSyncService.getMerchSynchronization(operationId).isEmpty())
		{
			merchSyncService.createMerchSychronization(config, operationId, context.getIndexerOperationType().name());
		}
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
			handleError(operationId, step, e);
		}
		cleanLogContextOperationId();
	}

	protected void handleError(final String operationId, final String step, final Exception e)
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

	protected boolean isMerchSyncFailed(final String operationId)
	{
		return merchSyncService.isMerchSyncFailed(operationId);
	}

	protected void completeSyncProcess(final SnIndexerContext indexerContext)
	{
		getProductDirectoryConfig(indexerContext.getIndexType().getId())
				.ifPresent(config -> {
					final String operationId = getOperationId(indexerContext);
					merchSyncService.completeMerchSyncProcess(operationId, getNumberOfProducts(operationId));
					invalidateMerchContext(indexerContext);
				});
	}

	protected void saveBatchInfo(final MerchSnSynchContext merchContext, final long productNumber)
	{
		if (merchContext != null)
		{
			merchContext.addProducts(productNumber);
		}
	}

	protected Long getNumberOfProducts(final String operationId)
	{
		final MerchSnSynchContext context = merchContextCache.getIfPresent(operationId);
		return context == null ? 0 : context.getNumberOfProducts();
	}

	protected void setLogContextOperationId(final String operationId)
	{
		MDC.put(LOG_CONTEXT_MERCH_OPERATION_ID, operationId);
	}

	protected void cleanLogContextOperationId()
	{
		MDC.remove(LOG_CONTEXT_MERCH_OPERATION_ID);
	}


	// Getters and setters
	protected MerchSnConfigService getMerchSnConfigService()
	{
		return merchSnConfigService;
	}

	public void setMerchSnConfigService(final MerchSnConfigService merchSnConfigService)
	{
		this.merchSnConfigService = merchSnConfigService;
	}

	protected MerchCatalogService getMerchCatalogService()
	{
		return merchCatalogService;
	}

	public void setMerchCatalogService(final MerchCatalogService merchCatalogService)
	{
		this.merchCatalogService = merchCatalogService;
	}

	protected MerchCatalogServiceProductDirectoryClient getMerchCatalogServiceProductDirectoryClient()
	{
		return merchCatalogServiceProductDirectoryClient;
	}

	public void setMerchCatalogServiceProductDirectoryClient(final MerchCatalogServiceProductDirectoryClient merchCatalogServiceProductDirectoryClient)
	{
		this.merchCatalogServiceProductDirectoryClient = merchCatalogServiceProductDirectoryClient;
	}

	protected MerchSyncService getMerchSyncService()
	{
		return merchSyncService;
	}

	public void setMerchSyncService(final MerchSyncService merchSyncService)
	{
		this.merchSyncService = merchSyncService;
	}

	protected ImpersonationService getImpersonationService()
	{
		return impersonationService;
	}

	public void setImpersonationService(final ImpersonationService impersonationService)
	{
		this.impersonationService = impersonationService;
	}

	protected SnSearchService getSnSearchService()
	{
		return snSearchService;
	}

	public void setSnSearchService(final SnSearchService snSearchService)
	{
		this.snSearchService = snSearchService;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected interface Method
	{
		void execute() throws Exception;
	}
}
