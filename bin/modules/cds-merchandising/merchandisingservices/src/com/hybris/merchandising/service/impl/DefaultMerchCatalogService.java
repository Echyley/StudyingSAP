/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.service.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.searchservices.document.data.SnDocument;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchOperationRequest;
import de.hybris.platform.searchservices.enums.SnDocumentOperationType;
import de.hybris.platform.searchservices.enums.SnIndexerOperationType;
import de.hybris.platform.searchservices.indexer.service.SnIndexerBatchContext;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.model.config.SolrIndexedPropertyModel;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.search.FacetField;
import de.hybris.platform.solrfacetsearch.search.FacetSearchException;
import de.hybris.platform.solrfacetsearch.search.FacetSearchService;
import de.hybris.platform.solrfacetsearch.search.FieldNameTranslator;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import de.hybris.platform.solrfacetsearch.search.SearchQueryCurrencyResolver;
import de.hybris.platform.solrfacetsearch.search.SearchQueryLanguageResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.hybris.merchandising.model.AbstractMerchPropertyModel;
import com.hybris.merchandising.model.IndexedPropertyInfo;
import com.hybris.merchandising.model.MerchProductDirectoryConfigModel;
import com.hybris.merchandising.model.MerchPropertyModel;
import com.hybris.merchandising.model.MerchSnConfigModel;
import com.hybris.merchandising.model.MerchSnDocumentContainer;
import com.hybris.merchandising.model.MerchSnSynchContext;
import com.hybris.merchandising.model.Product;
import com.hybris.merchandising.model.ProductIndexContainer;
import com.hybris.merchandising.service.MerchCatalogService;
import com.hybris.platform.merchandising.yaas.CategoryHierarchy;

/**
 * DefaultMerchCatalogService is a default implementation of {@link MerchCatalogService}, used
 * to make catalog requests for Merch v2 support.
 */
public class DefaultMerchCatalogService implements MerchCatalogService
{
	private static final String CREATE_ACTION = "CREATE";
	protected static final String DEFAULT_QUERY_TEMPLATE = "DEFAULT";
	protected static final String EXECUTE = "execute";
	protected static final String CATALOG_VERSION = "catalogVersion";
	protected static final String CATALOG_VERSION_SEPARATOR = ":";
	protected static final String CATALOG_ID = "catalogId";


	protected FacetSearchService facetSearchService;
	protected FieldNameTranslator fieldNameTranslator;
	protected SearchQueryLanguageResolver searchQueryLanguageResolver;
	protected SearchQueryCurrencyResolver searchQueryCurrencyResolver;
	protected BaseSiteService baseSiteService;
	protected AbstractUrlResolver<CategoryModel> categoryUrlResolver;
	protected Converter<ProductIndexContainer, Product> merchProductConverter;
	protected Converter<MerchSnDocumentContainer, Product> merchSnProductConverter;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CategoryHierarchy> getCategories(final String baseSite, final String catalogId, final String catalogVersionId,
	                                             final String baseCatalogPageUrl)
	{
		final BaseSiteModel baseSiteModel = baseSiteService.getBaseSiteForUID(baseSite);
		return exportCategories(baseSiteModel, baseCatalogPageUrl);
	}

	@Override
	public List<Product> getProducts(final IndexerBatchContext indexerBatchContext,
	                                 final MerchProductDirectoryConfigModel merchProductDirectoryConfigModel)
			throws IndexerException
	{
		final SearchQuery searchQuery = createSearchQuery(indexerBatchContext, merchProductDirectoryConfigModel);

		final Map<String, IndexedPropertyInfo> indexedPropertiesMapping = createIndexedPropertiesMapping(indexerBatchContext,
				searchQuery);

		final Map<String, FacetField> merchFacetPropertiesMapping = createMerchFacetPropertiesMapping(indexerBatchContext,
				searchQuery);

		final Map<String, String> merchPropertiesMapping = createMerchPropertiesMapping(
				merchProductDirectoryConfigModel.getMerchProperties(), indexedPropertiesMapping);

		return indexerBatchContext.getInputDocuments().stream()
		                          .filter(inputDocument -> isToSynchronize(
				                          merchProductDirectoryConfigModel.getMerchCatalogVersions(), inputDocument))
		                          .map(document ->
		                          {
			                          final ProductIndexContainer productIndexContainer = ProductIndexContainer.builder()
			                                                                                                   .withIndexedPropertiesMapping(indexedPropertiesMapping)
			                                                                                                   .withMerchFacetPropertiesMapping(
					                                                                                                   merchFacetPropertiesMapping)
			                                                                                                   .withMerchPropertiesMapping(merchPropertiesMapping)
			                                                                                                   .withInputDocument(document)
			                                                                                                   .withMerchProductDirectoryConfigModel(
					                                                                                                   merchProductDirectoryConfigModel)
			                                                                                                   .withSearchQuery(searchQuery)
			                                                                                                   .build();
			                          final Product finalProduct = merchProductConverter.convert(productIndexContainer);
			                          finalProduct.setAction(
					                          createActionForIndexOperation(indexerBatchContext.getIndexOperation()));
			                          return finalProduct;
		                          }).collect(Collectors.toList());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CategoryHierarchy> getCategories(final MerchProductDirectoryConfigModel config) throws IndexerException
	{
		//As a product directory config model can support > 1 base site, we simply retrieve the first base site and use that to export the 
		//hierarchy.
		final BaseSiteModel baseSiteModel = config.getBaseSites()
		                                          .stream()
		                                          .findAny()
		                                          .orElseThrow(
				                                          () -> new IndexerException("Config does not have any base sites set"));
		return exportCategories(baseSiteModel, config.getBaseCatalogPageUrl());
	}

	@Override
	public List<Product> getProducts(final SnIndexerBatchContext indexerBatchContext, final MerchSnConfigModel merchConfig, final MerchSnSynchContext merchContext)
	{
		final List<CatalogVersionModel> catalogVersionsToSync = getCatalogVersionsToSynch(merchConfig);
		final List<SnDocumentBatchOperationRequest> documentList = indexerBatchContext.getIndexerBatchResponse().getDocumentBatchRequest().getRequests();
		final MerchSnDocumentContainer documentContainer = MerchSnDocumentContainer.from(indexerBatchContext, merchContext, merchConfig);

		return documentList.stream()
		                   .filter(docRequest -> isToSynchronize(catalogVersionsToSync, docRequest.getDocument()))
		                   .map(docRequest ->
		                   {
			                   documentContainer.setInputDocument(docRequest.getDocument());
			                   final Product finalProduct = merchSnProductConverter.convert(documentContainer);
			                   finalProduct.setAction(
					                   getActionForIndexOperation(indexerBatchContext.getIndexerOperationType(),
							                   docRequest.getOperationType()));
			                   return finalProduct;
		                   })
		                   .collect(Collectors.toList());

	}

	@Override
	public List<CategoryHierarchy> getCategories(final MerchSnConfigModel merchSnConfig)
	{
		return exportCategories(merchSnConfig.getBaseSite(), merchSnConfig.getBaseCatalogPageUrl());
	}

	@Override
	public List<CatalogVersionModel> getCatalogVersionsToSynch(final MerchSnConfigModel merchSnConfig)
	{
		final Stream<CatalogVersionModel> cv1 = merchSnConfig.getSnIndexType()
		                                                     .getCatalogs()
		                                                     .stream()
		                                                     .flatMap(catalog -> catalog.getCatalogVersions().stream())
		                                                     .filter(CatalogVersionModel::getActive);

		final Stream<CatalogVersionModel> cv2 = merchSnConfig.getSnIndexType()
		                                                     .getCatalogVersions()
		                                                     .stream()
		                                                     .filter(CatalogVersionModel::getActive);
		return Stream.concat(cv1, cv2)
		             .collect(Collectors.toList());
	}

	/**
	 * Method is used to generate an instance of {@link SearchQuery} to query Solr to retrieve updated product
	 * information.
	 *
	 * @param indexerBatchContext represents a context valid for the duration of an indexer batch.
	 * @param productDirectory    - this is the product directory we wish to export products for.
	 * @return SearchQuery
	 */
	protected SearchQuery createSearchQuery(final IndexerBatchContext indexerBatchContext,
	                                        final MerchProductDirectoryConfigModel productDirectory)
	{
		final SearchQuery searchQuery = facetSearchService.createSearchQueryFromTemplate(
				indexerBatchContext.getFacetSearchConfig(), indexerBatchContext.getIndexedType(), DEFAULT_QUERY_TEMPLATE);

		// Default language is resolved based on merch configuration or FacetSearchConfig and/or IndexedType
		final LanguageModel language = getDefaultLanguage(indexerBatchContext, productDirectory);

		searchQuery.setLanguage(language.getIsocode());
		searchQuery.setCurrency(Optional.ofNullable(productDirectory.getCurrency()).map(CurrencyModel::getIsocode)
		                                .orElseGet(() -> searchQueryCurrencyResolver.resolveCurrency(
				                                indexerBatchContext.getFacetSearchConfig(),
				                                indexerBatchContext.getIndexedType()).getIsocode()));
		return searchQuery;
	}

	/**
	 * Method return default language based on product directory configuration and index configuration
	 *
	 * @param indexerBatchContext    indexing context
	 * @param productDirectoryConfig product dirctory configuration
	 * @return default language
	 */
	protected LanguageModel getDefaultLanguage(final IndexerBatchContext indexerBatchContext,
	                                           final MerchProductDirectoryConfigModel productDirectoryConfig)
	{
		final String langIsoCode = productDirectoryConfig.getDefaultLanguage().getIsocode();

		return indexerBatchContext.getFacetSearchConfig().getIndexConfig().getLanguages()
		                          .stream()
		                          .filter(l -> StringUtils.equals(l.getIsocode(), langIsoCode))
		                          .findFirst()
		                          .orElse(searchQueryLanguageResolver.resolveLanguage(indexerBatchContext.getFacetSearchConfig(),
				                          indexerBatchContext.getIndexedType()));

	}

	/**
	 * Method used to retrieve a map of configured key value pairs for handling mapping between
	 * Solr internal data model
	 *
	 * @param batchContext indexerBatchContext represents a context valid for the duration of an indexer batch.
	 * @param searchQuery
	 * @return
	 */
	protected Map<String, IndexedPropertyInfo> createIndexedPropertiesMapping(final IndexerBatchContext batchContext,
	                                                                          final SearchQuery searchQuery)
	{
		return batchContext.getIndexedProperties().stream()
		                   .filter(Objects::nonNull)
		                   .collect(Collectors
				                   .toMap(IndexedProperty::getName,
						                   indexedProperty -> createIndexedPropertyInfo(searchQuery, indexedProperty)));
	}

	/**
	 * Method to determine whether a given document is for the Online or Staging
	 * catalog.
	 *
	 * @param catalogVersionsToExport
	 * @param document
	 * @return
	 */
	protected boolean isToSynchronize(final List<CatalogVersionModel> catalogVersionsToExport, final InputDocument document)
	{
		for (final CatalogVersionModel catalogVersionModel : catalogVersionsToExport)
		{
			final String documentCatalogId = (String) document.getFieldValue(CATALOG_ID);
			final String documentCatalogVersion = (String) document.getFieldValue(CATALOG_VERSION);
			if ((documentCatalogId != null && documentCatalogVersion != null) &&
					(catalogVersionModel.getVersion().equals(documentCatalogVersion)
							&& catalogVersionModel.getCatalog().getId().equals(documentCatalogId)))
			{
				return true;
			}
		}
		return false;
	}


	/**
	 * Method to determine whether a given document should be synchronized
	 * This implementation assumes that fieldId for catalog version is "catalogVersion"
	 * and that format of value is catalogId:catalogVersion (like it is implemented  in CatalogVersionSnIndexerValueProvider)
	 *
	 * @param catalogVersionsToExport List of catalog version which should be synchronized
	 * @param document                indexed document
	 * @return true - should be synchronized , false - shouldn't be synchronized
	 */
	protected boolean isToSynchronize(final List<CatalogVersionModel> catalogVersionsToExport, final SnDocument document)
	{
		if (document == null)
		{
			return false;
		}

		final String documentCatalogVersion = (String) document.getFields().get(CATALOG_VERSION);
		if (documentCatalogVersion == null)
		{
			//we cannot filter data because there is no catalog version field
			return true;
		}

		for (final CatalogVersionModel catalogVersionModel : catalogVersionsToExport)
		{
			final String catalogVersion = catalogVersionModel.getCatalog().getId() + CATALOG_VERSION_SEPARATOR + catalogVersionModel.getVersion();
			if (StringUtils.equals(catalogVersion, documentCatalogVersion))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Helper method to populate IndexedPropertyInfo based on {@LanguageModel} configured under {@FacetSearchConfig}
	 *
	 * @param searchQuery
	 * @param indexedProperty
	 * @return
	 */
	protected IndexedPropertyInfo createIndexedPropertyInfo(final SearchQuery searchQuery, final IndexedProperty indexedProperty)
	{
		if (indexedProperty == null || searchQuery == null)
		{
			return null;
		}

		final String language = searchQuery.getLanguage();
		final List<String> translatedFiledNames = Optional.of(indexedProperty)
		                                                  .filter(IndexedProperty::isLocalized)
		                                                  .map(localisedProperty -> searchQuery.getFacetSearchConfig()
		                                                                                       .getIndexConfig()
		                                                                                       .getLanguages()
		                                                                                       .stream()
		                                                                                       .map(languageModel ->
		                                                                                       {
			                                                                                       searchQuery.setLanguage(
					                                                                                       languageModel.getIsocode());
			                                                                                       return getTranslatedFieldName(
					                                                                                       searchQuery,
					                                                                                       indexedProperty);
		                                                                                       })
		                                                                                       .collect(Collectors.toList()))
		                                                  .orElse(Arrays.asList(
				                                                  getTranslatedFieldName(searchQuery, indexedProperty)));

		searchQuery.setLanguage(language);
		return IndexedPropertyInfo.builder().withIndexedProperty(indexedProperty)
		                          .withTranslatedFieldNames(translatedFiledNames)
		                          .withTranslatedFieldName(getTranslatedFieldName(searchQuery, indexedProperty))
		                          .withLocalised(indexedProperty.isLocalized()).build();

	}

	protected String getTranslatedFieldName(final SearchQuery searchQuery, final IndexedProperty indexedProperty)
	{
		return fieldNameTranslator.translate(searchQuery, indexedProperty.getName(),
				FieldNameProvider.FieldType.INDEX);
	}

	/**
	 * createMerchFacetPropertiesMapping is a method for mapping facets to output for consumption by Merchandising.
	 *
	 * @param batchContext
	 * @param searchQuery
	 * @return
	 * @throws IndexerException
	 */
	protected Map<String, FacetField> createMerchFacetPropertiesMapping(final IndexerBatchContext batchContext,
	                                                                    final SearchQuery searchQuery)
			throws IndexerException
	{
		try
		{
			facetSearchService.search(searchQuery, Collections.singletonMap(EXECUTE, Boolean.FALSE.toString()));
		}
		catch (final FacetSearchException e)
		{
			throw new IndexerException(e);
		}

		return batchContext.getIndexedProperties().stream()
		                   .flatMap(index -> searchQuery.getFacets().stream()
		                                                .filter(prop -> prop.getField().equals(index.getName())))
		                   .collect(Collectors.toMap(FacetField::getField, Function.identity()));
	}

	/**
	 * createMerchPropertiesMapping is a method for mapping from the configured list of {@link MerchPropertyModel} to the indexed
	 * properties within Solr.
	 *
	 * @param merchProperties
	 * @param indexedPropertiesMapping
	 * @return
	 */
	protected Map<String, String> createMerchPropertiesMapping(final List<MerchPropertyModel> merchProperties,
	                                                           final Map<String, IndexedPropertyInfo> indexedPropertiesMapping)
	{
		return merchProperties.stream()
		                      .filter(merchProp -> Objects.nonNull(merchProp.getIndexedProperty()))
		                      .filter(merchProp -> indexedPropertiesMapping.containsKey(merchProp.getIndexedProperty().getName()))
		                      .collect(Collectors.toMap(this::extractPropTranslatedName, this::extractPropName));
	}

	protected String extractPropName(final AbstractMerchPropertyModel merchProp)
	{
		return merchProp.getIndexedProperty().getName();
	}

	protected String extractPropTranslatedName(final AbstractMerchPropertyModel merchProp)
	{
		final SolrIndexedPropertyModel indexedProperty = merchProp.getIndexedProperty();
		return StringUtils.isNotBlank(
				merchProp.getMerchMappedName()) ? merchProp.getMerchMappedName() : indexedProperty.getName();
	}

	/**
	 * Retrieves a list of {@link CategoryHierarchy} objects for the given parameters.
	 *
	 * @param baseSiteModel      the {@link BaseSiteModel} to extract the categories for.
	 * @param baseCatalogPageUrl the URL to append to the start of category URLs.
	 * @return a List representing the category hierarchy.
	 */
	private List<CategoryHierarchy> exportCategories(final BaseSiteModel baseSiteModel, final String baseCatalogPageUrl)
	{
		final List<CatalogModel> productCatalogs = baseSiteService.getProductCatalogs(baseSiteModel);
		final List<CategoryHierarchy> categories = new ArrayList<>();
		final String categoryUrl = StringUtils.isNotEmpty(baseCatalogPageUrl) ? baseCatalogPageUrl : "";

		productCatalogs.forEach(cm -> cm.getCatalogVersions()
		                                .stream().filter(CatalogVersionModel::getActive)
		                                .forEach(version -> version.getRootCategories()
		                                                           .forEach(category ->
		                                                           {
			                                                           final CategoryHierarchy rootCategory = new CategoryHierarchy();
			                                                           rootCategory.setId(category.getCode());
			                                                           rootCategory.setName(category.getName());
			                                                           rootCategory.setSubcategories(new ArrayList<>());
			                                                           rootCategory.setUrl(getUrl(category, categoryUrl));
			                                                           processSubCategories(rootCategory, category, true,
					                                                           categoryUrl);
			                                                           categories.add(rootCategory);
		                                                           })));
		return categories;
	}


	/**
	 * processSubCategories is a method for taking a category model and recursively processing its sub categories.
	 *
	 * @param toPopulate  the {@link CategoryHierarchy} we wish to populate.
	 * @param category    the {@link CategoryModel} representing the hierarchy.
	 * @param root        whether the category is a root category or not.
	 * @param categoryUrl the URL to prefix the category with.
	 */
	protected void processSubCategories(final CategoryHierarchy toPopulate, final CategoryModel category, final boolean root,
	                                    final String categoryUrl)
	{
		if (root)
		{
			category.getCategories().forEach(subCategory -> processSubCategories(toPopulate, subCategory, false, categoryUrl));
		}
		else
		{
			final CategoryHierarchy hierarchy = new CategoryHierarchy();
			hierarchy.setId(category.getCode());
			hierarchy.setName(category.getName());
			hierarchy.setSubcategories(new ArrayList<>());
			hierarchy.setUrl(getUrl(category, categoryUrl));
			toPopulate.getSubcategories().add(hierarchy);
			category.getCategories().forEach(subCategory -> processSubCategories(hierarchy, subCategory, false, categoryUrl));
		}
	}

	/**
	 * createActionForIndexOperation is a method for retrieving the action to use when sending to Merchandising
	 * for a given {@code IndexOperation}.
	 *
	 * @param indexOperation the operation to retrieve the value for.
	 * @return the action to use.
	 */
	protected String createActionForIndexOperation(final IndexOperation indexOperation)
	{
		switch (indexOperation)
		{
			case FULL:
				return CREATE_ACTION;
			case UPDATE:
			case PARTIAL_UPDATE:
				return "UPDATE";
			case DELETE:
				return "DELETE";
		}
		return null;
	}

	protected String getActionForIndexOperation(final SnIndexerOperationType indexOperation,
	                                            final SnDocumentOperationType operationType)
	{
		if (SnIndexerOperationType.FULL.equals(indexOperation))
		{
			return CREATE_ACTION;
		}

		switch (operationType)
		{
			case CREATE:
				return CREATE_ACTION;
			case CREATE_UPDATE:
			case PARTIAL_UPDATE:
				return "UPDATE";
			case DELETE:
				return "DELETE";
		}
		return null;
	}

	/**
	 * getUrl resolves the URL for a provided {@link CategoryModel}.
	 *
	 * @param category the category model to retrieve the URL for.
	 * @return a String containing the URL.
	 */
	protected String getUrl(final CategoryModel category, final String baseCategoryUrl)
	{
		final String resolvedUrl = categoryUrlResolver.resolve(category);
		if (StringUtils.isNotEmpty(resolvedUrl))
		{
			return baseCategoryUrl + resolvedUrl;
		}
		return resolvedUrl;
	}

	/**
	 * Gets the configured {@link BaseSiteService}, used to set the current site for the request.
	 *
	 * @return the injected base site service to use.
	 */
	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * Sets the configured {@link BaseSiteService}, used to set the current site for the request.
	 *
	 * @param baseSiteService the injected base site service to use.
	 */
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * Retrieves the configured URL resolver for {@link CategoryModel}.
	 *
	 * @return the configured resolver.
	 */
	public AbstractUrlResolver<CategoryModel> getCategoryUrlResolver()
	{
		return categoryUrlResolver;
	}

	/**
	 * Sets the configured URL resolver for {@link CategoryModel}.
	 *
	 * @param categoryUrlResolver the resolver to return.
	 */
	public void setCategoryUrlResolver(final AbstractUrlResolver<CategoryModel> categoryUrlResolver)
	{
		this.categoryUrlResolver = categoryUrlResolver;
	}

	public void setFacetSearchService(final FacetSearchService facetSearchService)
	{
		this.facetSearchService = facetSearchService;
	}

	public void setSearchQueryLanguageResolver(final SearchQueryLanguageResolver searchQueryLanguageResolver)
	{
		this.searchQueryLanguageResolver = searchQueryLanguageResolver;
	}

	public void setSearchQueryCurrencyResolver(final SearchQueryCurrencyResolver searchQueryCurrencyResolver)
	{
		this.searchQueryCurrencyResolver = searchQueryCurrencyResolver;
	}

	public void setFieldNameTranslator(final FieldNameTranslator fieldNameTranslator)
	{
		this.fieldNameTranslator = fieldNameTranslator;
	}

	public void setMerchProductConverter(final Converter<ProductIndexContainer, Product> merchProductConverter)
	{
		this.merchProductConverter = merchProductConverter;
	}

	public void setMerchSnProductConverter(
			final Converter<MerchSnDocumentContainer, Product> merchSnProductConverter)
	{
		this.merchSnProductConverter = merchSnProductConverter;
	}
}
