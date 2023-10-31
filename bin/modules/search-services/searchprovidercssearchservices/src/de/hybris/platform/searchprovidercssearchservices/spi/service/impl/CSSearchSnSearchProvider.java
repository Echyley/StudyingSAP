/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchprovidercssearchservices.spi.service.impl;

import static de.hybris.platform.searchservices.util.ConverterUtils.convert;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.factory.ClientFactory;
import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;
import de.hybris.platform.apiregistryservices.services.DestinationService;
import de.hybris.platform.searchprovidercssearchservices.admin.data.FieldDTO;
import de.hybris.platform.searchprovidercssearchservices.admin.data.FieldTypeDTO;
import de.hybris.platform.searchprovidercssearchservices.admin.data.IndexConfigurationDTO;
import de.hybris.platform.searchprovidercssearchservices.admin.data.IndexDTO;
import de.hybris.platform.searchprovidercssearchservices.admin.data.IndexStatusDTO;
import de.hybris.platform.searchprovidercssearchservices.admin.data.IndexTypeDTO;
import de.hybris.platform.searchprovidercssearchservices.admin.data.LanguageDTO;
import de.hybris.platform.searchprovidercssearchservices.admin.data.QualifierDTO;
import de.hybris.platform.searchprovidercssearchservices.admin.data.QualifierTypeDTO;
import de.hybris.platform.searchprovidercssearchservices.admin.data.SearchToleranceDTO;
import de.hybris.platform.searchprovidercssearchservices.admin.data.SynonymDictionaryDTO;
import de.hybris.platform.searchprovidercssearchservices.admin.data.SynonymEntryDTO;
import de.hybris.platform.searchprovidercssearchservices.constants.SearchprovidercssearchservicesConstants;
import de.hybris.platform.searchprovidercssearchservices.document.data.DocumentBatchOperationRequestDTO;
import de.hybris.platform.searchprovidercssearchservices.document.data.DocumentBatchOperationResponseDTO;
import de.hybris.platform.searchprovidercssearchservices.document.data.DocumentBatchRequestDTO;
import de.hybris.platform.searchprovidercssearchservices.document.data.DocumentBatchResponseDTO;
import de.hybris.platform.searchprovidercssearchservices.document.data.DocumentDTO;
import de.hybris.platform.searchprovidercssearchservices.index.data.AbstractIndexStateResponseDTO;
import de.hybris.platform.searchprovidercssearchservices.indexer.data.AbortIndexerOperationStateRequestDTO;
import de.hybris.platform.searchprovidercssearchservices.indexer.data.CommitIndexStateRequestDTO;
import de.hybris.platform.searchprovidercssearchservices.indexer.data.CompleteIndexerOperationStateRequestDTO;
import de.hybris.platform.searchprovidercssearchservices.indexer.data.FailIndexerOperationStateRequestDTO;
import de.hybris.platform.searchprovidercssearchservices.indexer.data.IndexerOperationDTO;
import de.hybris.platform.searchprovidercssearchservices.indexer.data.IndexerOperationStatusDTO;
import de.hybris.platform.searchprovidercssearchservices.indexer.data.IndexerOperationTypeDTO;
import de.hybris.platform.searchprovidercssearchservices.indexer.data.TaskIndexStateResponseDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.AbstractExpressionQueryDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.AndQueryDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.BucketsFacetFilterDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.EqualQueryDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.ExistsQueryDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.GreaterThanOrEqualQueryDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.GreaterThanQueryDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.LessThanOrEqualQueryDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.LessThanQueryDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.MatchQueryDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.MatchTermQueryDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.MatchTermsQueryDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.NotEqualQueryDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.NotQueryDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.OrQueryDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.PromotedHitsRankRuleDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.QueryFunctionRankRuleDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.RangeBucketsFacetRequestDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.RangeBucketsFacetResponseDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.RangeQueryDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.SearchQueryDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.SearchResultDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.SortOrderDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.TermBucketsFacetRequestDTO;
import de.hybris.platform.searchprovidercssearchservices.search.data.TermBucketsFacetResponseDTO;
import de.hybris.platform.searchprovidercssearchservices.spi.data.CSSearchSnSearchProviderConfiguration;
import de.hybris.platform.searchprovidercssearchservices.spi.service.CSSearchClient;
import de.hybris.platform.searchprovidercssearchservices.suggest.data.SuggestQueryDTO;
import de.hybris.platform.searchprovidercssearchservices.suggest.data.SuggestResultDTO;
import de.hybris.platform.searchprovidercssearchservices.task.data.AsyncTaskResponseDTO;
import de.hybris.platform.searchprovidercssearchservices.task.data.TaskDTO;
import de.hybris.platform.searchprovidercssearchservices.task.data.TaskStatusDTO;
import de.hybris.platform.searchservices.admin.data.SnCurrency;
import de.hybris.platform.searchservices.admin.data.SnField;
import de.hybris.platform.searchservices.admin.data.SnIndexConfiguration;
import de.hybris.platform.searchservices.admin.data.SnIndexType;
import de.hybris.platform.searchservices.admin.data.SnLanguage;
import de.hybris.platform.searchservices.admin.data.SnSynonymDictionary;
import de.hybris.platform.searchservices.admin.data.SnSynonymEntry;
import de.hybris.platform.searchservices.constants.SearchservicesConstants;
import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.core.SnRuntimeException;
import de.hybris.platform.searchservices.core.service.SnContext;
import de.hybris.platform.searchservices.core.service.SnQualifier;
import de.hybris.platform.searchservices.document.data.SnDocument;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchOperationRequest;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchOperationResponse;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchRequest;
import de.hybris.platform.searchservices.document.data.SnDocumentBatchResponse;
import de.hybris.platform.searchservices.enums.SnDocumentOperationStatus;
import de.hybris.platform.searchservices.enums.SnDocumentOperationType;
import de.hybris.platform.searchservices.enums.SnFieldType;
import de.hybris.platform.searchservices.enums.SnIndexerOperationStatus;
import de.hybris.platform.searchservices.enums.SnIndexerOperationType;
import de.hybris.platform.searchservices.enums.SnSearchTolerance;
import de.hybris.platform.searchservices.index.data.SnIndex;
import de.hybris.platform.searchservices.indexer.data.SnIndexerOperation;
import de.hybris.platform.searchservices.search.data.AbstractSnExpressionQuery;
import de.hybris.platform.searchservices.search.data.SnAndQuery;
import de.hybris.platform.searchservices.search.data.SnBucketsFacetFilter;
import de.hybris.platform.searchservices.search.data.SnEqualQuery;
import de.hybris.platform.searchservices.search.data.SnExistsQuery;
import de.hybris.platform.searchservices.search.data.SnGreaterThanOrEqualQuery;
import de.hybris.platform.searchservices.search.data.SnGreaterThanQuery;
import de.hybris.platform.searchservices.search.data.SnLessThanOrEqualQuery;
import de.hybris.platform.searchservices.search.data.SnLessThanQuery;
import de.hybris.platform.searchservices.search.data.SnMatchQuery;
import de.hybris.platform.searchservices.search.data.SnMatchTermQuery;
import de.hybris.platform.searchservices.search.data.SnMatchTermsQuery;
import de.hybris.platform.searchservices.search.data.SnNotEqualQuery;
import de.hybris.platform.searchservices.search.data.SnNotQuery;
import de.hybris.platform.searchservices.search.data.SnOrQuery;
import de.hybris.platform.searchservices.search.data.SnPromotedHitsRankRule;
import de.hybris.platform.searchservices.search.data.SnQueryFunctionRankRule;
import de.hybris.platform.searchservices.search.data.SnRangeBucketsFacetRequest;
import de.hybris.platform.searchservices.search.data.SnRangeBucketsFacetResponse;
import de.hybris.platform.searchservices.search.data.SnRangeQuery;
import de.hybris.platform.searchservices.search.data.SnSearchQuery;
import de.hybris.platform.searchservices.search.data.SnSearchResult;
import de.hybris.platform.searchservices.search.data.SnSortOrder;
import de.hybris.platform.searchservices.search.data.SnTermBucketsFacetRequest;
import de.hybris.platform.searchservices.search.data.SnTermBucketsFacetResponse;
import de.hybris.platform.searchservices.spi.data.SnExportConfiguration;
import de.hybris.platform.searchservices.spi.service.SnSearchProvider;
import de.hybris.platform.searchservices.spi.service.impl.AbstractSnSearchProvider;
import de.hybris.platform.searchservices.suggest.data.SnSuggestQuery;
import de.hybris.platform.searchservices.suggest.data.SnSuggestResult;
import de.hybris.platform.searchservices.util.ConverterUtils;
import de.hybris.platform.searchservices.util.JsonUtils;
import de.hybris.platform.searchservices.util.RetryConfiguration;
import de.hybris.platform.searchservices.util.RetryUtils;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;

import com.hybris.charon.RawResponse;
import com.hybris.charon.exp.HttpException;
import com.hybris.charon.exp.NotFoundException;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;


/**
 * Implementation of {@link SnSearchProvider} for the search core service.
 */
public class CSSearchSnSearchProvider extends AbstractSnSearchProvider<CSSearchSnSearchProviderConfiguration>
		implements InitializingBean
{
	protected static final String CREATE_INDEX_FAILED_MSG = "Create index failed";

	protected static final String CREATE_UPDATE_INDEX_TYPE_FAILED_MSG = "Create or update indexType failed";
	protected static final String DELETE_INDEX_FAILED_MSG = "Delete index failed";
	protected static final String COMMIT_INDEX_FAILED_MSG = "Index commit failed";
	protected static final String CREATE_INDEXER_OPERATION_FAILED_MSG = "Create indexer operation failed";
	protected static final String COMPLETE_INDEXER_OPERATION_FAILED_MSG = "Complete indexer operation failed";
	protected static final String ABORT_INDEXER_OPERATION_FAILED_MSG = "Abort indexer operation failed";
	protected static final String FAIL_INDEXER_OPERATION_FAILED_MSG = "Fail indexer operation failed";
	protected static final String DOCUMENT_BATCH_FAILED_MSG = "Document batch failed";

	private static final Logger LOG = Logger.getLogger(CSSearchSnSearchProvider.class);

	public static final RetryConfiguration INDEX_RETRY_CONFIGURATION = RetryConfiguration.builder().withMaxAttempts(40)
			.withTimeout(Duration.ofSeconds(600)).withInterval(Duration.ofSeconds(1)).withMaxInterval(Duration.ofSeconds(15))
			.withMultiplier(Double.valueOf(2)).build();

	public static final RetryConfiguration INDEX_COMMIT_RETRY_CONFIGURATION = RetryConfiguration.builder().withMaxAttempts(60)
			.withTimeout(Duration.ofSeconds(900)).withInterval(Duration.ofSeconds(1)).withMaxInterval(Duration.ofSeconds(15))
			.withMultiplier(Double.valueOf(2)).build();

	public static final RetryConfiguration INDEXER_OPERATION_RETRY_CONFIGURATION = RetryConfiguration.builder().withMaxAttempts(40)
			.withTimeout(Duration.ofSeconds(600)).withInterval(Duration.ofSeconds(1)).withMaxInterval(Duration.ofSeconds(15))
			.withMultiplier(Double.valueOf(2)).build();

	public static final RetryConfiguration DOCUMENT_BATCH_RETRY_CONFIGURATION = RetryConfiguration.builder().withMaxAttempts(40)
			.withTimeout(Duration.ofSeconds(600)).withInterval(Duration.ofSeconds(1)).withMaxInterval(Duration.ofSeconds(15))
			.withMultiplier(Double.valueOf(2)).build();

	private DestinationService<ConsumedDestinationModel> destinationService;
	private ApiRegistryClientService apiRegistryClientService;
	private CommonI18NService commonI18NService;
	private ClientFactory clientFactory;

	private MapperFacade mapperFacade;

	@Override
	public void afterPropertiesSet()
	{
		final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		registerMappings(mapperFactory);
		mapperFacade = mapperFactory.getMapperFacade();
	}

	protected void registerMappings(final MapperFactory mapperFactory)
	{
		mapperFactory.registerMapper(new ListCustomMapper());
		mapperFactory.registerMapper(new MapCustomMapper());
		mapperFactory.getConverterFactory().registerConverter(new LocaleConverter());
		mapperFactory.getConverterFactory().registerConverter(new SortOrderConverter());

		// map query types
		mapperFactory.classMap(AbstractSnExpressionQuery.class, AbstractExpressionQueryDTO.class).byDefault()
				.field("language", "languageId").register();
		mapperFactory.classMap(SnAndQuery.class, AndQueryDTO.class).byDefault().register();
		mapperFactory.classMap(SnOrQuery.class, OrQueryDTO.class).byDefault().register();
		mapperFactory.classMap(SnNotQuery.class, NotQueryDTO.class).byDefault().register();
		mapperFactory.classMap(SnMatchTermQuery.class, MatchTermQueryDTO.class).byDefault().register();
		mapperFactory.classMap(SnMatchTermsQuery.class, MatchTermsQueryDTO.class).byDefault().register();
		mapperFactory.classMap(SnMatchQuery.class, MatchQueryDTO.class).byDefault().register();
		mapperFactory.classMap(SnEqualQuery.class, EqualQueryDTO.class).byDefault().register();
		mapperFactory.classMap(SnNotEqualQuery.class, NotEqualQueryDTO.class).byDefault().register();
		mapperFactory.classMap(SnGreaterThanOrEqualQuery.class, GreaterThanOrEqualQueryDTO.class).byDefault().register();
		mapperFactory.classMap(SnGreaterThanQuery.class, GreaterThanQueryDTO.class).byDefault().register();
		mapperFactory.classMap(SnLessThanOrEqualQuery.class, LessThanOrEqualQueryDTO.class).byDefault().register();
		mapperFactory.classMap(SnLessThanQuery.class, LessThanQueryDTO.class).byDefault().register();
		mapperFactory.classMap(SnRangeQuery.class, RangeQueryDTO.class).byDefault().register();
		mapperFactory.classMap(SnExistsQuery.class, ExistsQueryDTO.class).byDefault().register();

		// map rank rules
		mapperFactory.classMap(SnQueryFunctionRankRule.class, QueryFunctionRankRuleDTO.class).byDefault().register();
		mapperFactory.classMap(SnPromotedHitsRankRule.class, PromotedHitsRankRuleDTO.class).byDefault().register();

		// map facets
		mapperFactory.classMap(SnTermBucketsFacetRequest.class, TermBucketsFacetRequestDTO.class).byDefault().register();
		mapperFactory.classMap(SnRangeBucketsFacetRequest.class, RangeBucketsFacetRequestDTO.class).byDefault().register();
		mapperFactory.classMap(SnTermBucketsFacetResponse.class, TermBucketsFacetResponseDTO.class).byDefault().register();
		mapperFactory.classMap(SnRangeBucketsFacetResponse.class, RangeBucketsFacetResponseDTO.class).byDefault().register();
		mapperFactory.classMap(SnBucketsFacetFilter.class, BucketsFacetFilterDTO.class).byDefault().register();
	}

	protected MapperFacade getMapperFacade()
	{
		return mapperFacade;
	}

	@Override
	public void exportConfiguration(final SnExportConfiguration exportConfiguration, final List<Locale> locales) throws SnException
	{
		final ConverterContext converterContext = new ConverterContext(locales);
		final CSSearchClient client = createClient(exportConfiguration.getIndexConfiguration());
		exportSynonymDictionaries(converterContext, exportConfiguration.getSynonymDictionaries(), client);
		exportIndexConfiguration(converterContext, exportConfiguration.getIndexConfiguration(), client);
		exportIndexTypes(converterContext, exportConfiguration.getIndexTypes(), client);
	}

	protected void exportSynonymDictionaries(final ConverterContext converterContext,
			final List<SnSynonymDictionary> synonymDictionaries, final CSSearchClient client) throws SnException
	{
		if (CollectionUtils.isEmpty(synonymDictionaries))
		{
			return;
		}

		for (final SnSynonymDictionary synonymDictionary : synonymDictionaries)
		{
			final SynonymDictionaryDTO synonymDictionaryDTO = convertSynonymDictionary(converterContext, synonymDictionary);
			handleRequestException("Create/Update synonym dictionary failed", () -> client
					.createOrUpdateSynonymDictionary(synonymDictionaryDTO.getId(), synonymDictionaryDTO).toBlocking().subscribe());
		}
	}

	protected SynonymDictionaryDTO convertSynonymDictionary(final ConverterContext converterContext,
			final SnSynonymDictionary source)
	{
		final SynonymDictionaryDTO target = new SynonymDictionaryDTO();
		target.setId(source.getId());
		target.setName(buildLocalizedName(converterContext, source.getName()));
		target.setLanguageIds(source.getLanguageIds());
		target.setEntries(ConverterUtils.convertAll(source.getEntries(), this::convertSynonymEntry));

		return target;
	}

	protected SynonymEntryDTO convertSynonymEntry(final SnSynonymEntry source)
	{
		final SynonymEntryDTO target = new SynonymEntryDTO();
		target.setId(source.getId());
		target.setInput(source.getInput());
		target.setSynonyms(source.getSynonyms());

		return target;
	}

	protected void exportIndexConfiguration(final ConverterContext converterContext, final SnIndexConfiguration indexConfiguration,
			final CSSearchClient client) throws SnException
	{
		if (indexConfiguration == null)
		{
			return;
		}

		final IndexConfigurationDTO indexConfigurationDTO = convertIndexConfiguration(converterContext, indexConfiguration);
		handleRequestException("Create/Update index configuration failed", () -> client
				.createOrUpdateIndexConfiguration(indexConfigurationDTO.getId(), indexConfigurationDTO).toBlocking().subscribe());
	}

	protected IndexConfigurationDTO convertIndexConfiguration(final ConverterContext converterContext,
			final SnIndexConfiguration source)
	{
		final IndexConfigurationDTO target = new IndexConfigurationDTO();
		target.setId(source.getId());
		target.setName(buildLocalizedName(converterContext, source.getName()));
		target.setLanguages(ConverterUtils.convertAll(converterContext, source.getLanguages(), this::convertLanguage));
		target.setQualifierTypes(List.of(convertCurrencyQualifierType(converterContext, source)));
		target.setSynonymDictionaryIds(source.getSynonymDictionaryIds());

		return target;
	}

	protected LanguageDTO convertLanguage(final ConverterContext converterContext, final SnLanguage source)
	{
		final Locale locale = commonI18NService.getLocaleForIsoCode(source.getId());

		final LanguageDTO target = new LanguageDTO();
		target.setId(locale.toLanguageTag());
		target.setName(buildLocalizedName(converterContext, source.getName()));
		return target;
	}

	protected QualifierTypeDTO convertCurrencyQualifierType(final ConverterContext converterContext,
			final SnIndexConfiguration indexConfiguration)
	{
		final QualifierTypeDTO qualifierType = new QualifierTypeDTO();
		qualifierType.setId(SearchprovidercssearchservicesConstants.CURRENCY_QUALIFIER_TYPE_ID);
		qualifierType.setQualifiers(
				ConverterUtils.convertAll(converterContext, indexConfiguration.getCurrencies(), this::convertCurrencyQualifier));

		return qualifierType;
	}

	protected QualifierDTO convertCurrencyQualifier(final ConverterContext converterContext, final SnCurrency source)
	{
		final QualifierDTO target = new QualifierDTO();
		target.setId(source.getId());
		target.setName(buildLocalizedName(converterContext, source.getName()));
		return target;
	}

	protected void exportIndexTypes(final ConverterContext converterContext, final List<SnIndexType> indexTypes,
			final CSSearchClient client) throws SnException
	{
		if (CollectionUtils.isEmpty(indexTypes))
		{
			return;
		}

		for (final SnIndexType indexType : indexTypes)
		{
			final IndexTypeDTO indexTypeDTO = convertIndexType(converterContext, indexType);
			handleRequestException(CREATE_UPDATE_INDEX_TYPE_FAILED_MSG,
					() -> client.createOrUpdateIndexType(indexTypeDTO.getId(), indexTypeDTO).toBlocking().subscribe());
		}
	}

	protected IndexTypeDTO convertIndexType(final ConverterContext converterContext, final SnIndexType source)
	{
		final IndexTypeDTO target = new IndexTypeDTO();
		target.setId(source.getId());
		target.setName(buildLocalizedName(converterContext, source.getName()));
		target.setIndexConfigurationId(source.getIndexConfigurationId());
		target.setFields(ConverterUtils.convertAll(converterContext, source.getFields().values(), this::convertField));

		return target;
	}

	protected FieldDTO convertField(final ConverterContext converterContext, final SnField source)
	{
		final FieldDTO target = new FieldDTO();
		target.setId(source.getId());
		target.setName(buildLocalizedName(converterContext, source.getName()));
		target.setFieldType(convert(source.getFieldType(), this::convertFieldType));
		target.setRetrievable(source.getRetrievable());
		target.setSearchable(source.getSearchable());
		target.setSearchTolerance(convert(source.getSearchTolerance(), this::convertSearchTolerance));
		target.setLocalized(source.getLocalized());
		target.setQualifierTypeId(source.getQualifierTypeId());
		target.setMultiValued(source.getMultiValued());
		target.setUseForSuggesting(source.getUseForSuggesting());
		target.setUseForSpellchecking(source.getUseForSpellchecking());
		target.setWeight(source.getWeight());

		return target;
	}

	protected FieldTypeDTO convertFieldType(final SnFieldType source)
	{
		return FieldTypeDTO.valueOf(source.name());
	}

	protected SearchToleranceDTO convertSearchTolerance(final SnSearchTolerance source)
	{
		return SearchToleranceDTO.valueOf(source.name());
	}

	@Override
	public SnIndex createIndex(final SnContext context) throws SnException
	{
		final CSSearchClient client = createClient(context.getIndexConfiguration());

		final IndexDTO createdIndexDTO = handleRequestException(CREATE_INDEX_FAILED_MSG, () -> {
			final IndexDTO indexDTO = new IndexDTO();
			indexDTO.setIndexTypeId(context.getIndexType().getId());

			return client.createIndex(indexDTO).toBlocking().single().content().toBlocking().single();
		});

		final String indexId = createdIndexDTO.getId();

		final IndexDTO getIndexDTO = waitForIndexStatusOrElseThrow(client, indexId, IndexStatusDTO.PENDING,
				IndexStatusDTO.OPERATIONAL, CREATE_INDEX_FAILED_MSG);

		return convertIndex(getIndexDTO);
	}

	protected SnIndex convertIndex(final IndexDTO source)
	{
		final SnIndex target = new SnIndex();
		target.setId(source.getId());
		target.setIndexTypeId(source.getIndexTypeId());
		target.setActive(source.getActive());

		return target;
	}

	@Override
	public void deleteIndex(final SnContext context, final String indexId) throws SnException
	{
		final CSSearchClient client = createClient(context.getIndexConfiguration());

		handleRequestException(DELETE_INDEX_FAILED_MSG, () -> client.deleteIndex(indexId).toBlocking().subscribe());

		waitForIndexDeletionOrElseThrow(client, indexId, DELETE_INDEX_FAILED_MSG);
	}

	protected IndexDTO waitForIndexStatusOrElseThrow(final CSSearchClient client, final String indexId,
			final IndexStatusDTO pendingStatus, final IndexStatusDTO expectedStatus, final String errorMessage) throws SnException
	{
		final IndexDTO indexDTO = handleRequestException(errorMessage,
				() -> RetryUtils.waitForConditionOrElseThrow(
						() -> client.getIndex(indexId).toBlocking().single().content().toBlocking().single(),
						index -> index != null && index.getStatus() != pendingStatus, INDEX_RETRY_CONFIGURATION,
						indexerOperation -> new SnRuntimeException(errorMessage)));

		if (indexDTO.getStatus() != expectedStatus)
		{
			throw new SnException(errorMessage);
		}

		return indexDTO;
	}

	protected void waitForIndexDeletionOrElseThrow(final CSSearchClient client, final String indexId, final String errorMessage)
			throws SnException
	{
		handleRequestException(errorMessage, () -> RetryUtils.waitForConditionOrElseThrow(() -> {
			try
			{
				final RawResponse<IndexDTO> response = client.getIndex(indexId).toBlocking().single();
				return response.getStatusCode() == HttpStatus.NOT_FOUND.value();
			}
			catch (final NotFoundException e)
			{
				return true;
			}
		}, INDEX_RETRY_CONFIGURATION, () -> new SnRuntimeException(errorMessage)));
	}

	@Override
	public SnIndexerOperation createIndexerOperation(final SnContext context, final SnIndexerOperationType indexerOperationType,
			final int totalItems) throws SnException
	{
		final CSSearchClient client = createClient(context.getIndexConfiguration());

		final IndexerOperationDTO createIndexerOperationDTO = handleRequestException(CREATE_INDEXER_OPERATION_FAILED_MSG, () -> {
			final IndexerOperationDTO indexerOperationDTO = new IndexerOperationDTO();
			indexerOperationDTO.setIndexTypeId(context.getIndexType().getId());
			indexerOperationDTO.setOperationType(convert(indexerOperationType, this::convertIndexerOperationType));
			indexerOperationDTO.setTotalItems(Integer.valueOf(totalItems));

			return client.createIndexerOperation(indexerOperationDTO).toBlocking().single().content().toBlocking().single();
		});

		final String indexerOperationId = createIndexerOperationDTO.getId();

		final IndexerOperationDTO getIndexerOperationDTO = waitForIndexerOperationStatusOrElseThrow(client, indexerOperationId,
				IndexerOperationStatusDTO.PENDING, IndexerOperationStatusDTO.RUNNING, CREATE_INDEXER_OPERATION_FAILED_MSG);

		return convertIndexerOperation(getIndexerOperationDTO);
	}

	@Override
	public SnIndexerOperation updateIndexerOperationStatus(final SnContext context, final String indexerOperationId,
			final SnIndexerOperationStatus status, final String errorMessage) throws SnException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void completeIndexerOperation(final SnContext context, final String indexerOperationId) throws SnException
	{
		final CSSearchClient client = createClient(context.getIndexConfiguration());

		handleRequestException(COMPLETE_INDEXER_OPERATION_FAILED_MSG, () -> {
			final CompleteIndexerOperationStateRequestDTO indexerOperationStateRequestDTO = new CompleteIndexerOperationStateRequestDTO();
			client.executeIndexerOperationStateRequest(indexerOperationId, indexerOperationStateRequestDTO).toBlocking().subscribe();
		});

		waitForIndexerOperationStatusOrElseThrow(client, indexerOperationId, IndexerOperationStatusDTO.COMPLETING,
				IndexerOperationStatusDTO.COMPLETED, COMPLETE_INDEXER_OPERATION_FAILED_MSG);
	}

	@Override
	public void abortIndexerOperation(final SnContext context, final String indexerOperationId, final String message)
			throws SnException
	{
		final CSSearchClient client = createClient(context.getIndexConfiguration());

		handleRequestException(ABORT_INDEXER_OPERATION_FAILED_MSG, () -> {
			final AbortIndexerOperationStateRequestDTO indexerOperationStateRequestDTO = new AbortIndexerOperationStateRequestDTO();
			client.executeIndexerOperationStateRequest(indexerOperationId, indexerOperationStateRequestDTO).toBlocking().subscribe();
		});

		waitForIndexerOperationStatusOrElseThrow(client, indexerOperationId, IndexerOperationStatusDTO.ABORTING,
				IndexerOperationStatusDTO.ABORTED, ABORT_INDEXER_OPERATION_FAILED_MSG);
	}

	@Override
	public void failIndexerOperation(final SnContext context, final String indexerOperationId, final String message)
			throws SnException
	{
		final CSSearchClient client = createClient(context.getIndexConfiguration());

		handleRequestException(FAIL_INDEXER_OPERATION_FAILED_MSG, () -> {
			final FailIndexerOperationStateRequestDTO indexerOperationStateRequestDTO = new FailIndexerOperationStateRequestDTO();
			client.executeIndexerOperationStateRequest(indexerOperationId, indexerOperationStateRequestDTO).toBlocking().subscribe();
		});

		waitForIndexerOperationStatusOrElseThrow(client, indexerOperationId, IndexerOperationStatusDTO.FAILING,
				IndexerOperationStatusDTO.FAILED, FAIL_INDEXER_OPERATION_FAILED_MSG);
	}

	protected IndexerOperationDTO waitForIndexerOperationStatusOrElseThrow(final CSSearchClient client,
			final String indexerOperationId, final IndexerOperationStatusDTO pendingStatus,
			final IndexerOperationStatusDTO expectedStatus, final String errorMessage) throws SnException
	{
		final IndexerOperationDTO indexerOperationDTO = handleRequestException(errorMessage,
				() -> RetryUtils.waitForConditionOrElseThrow(
						() -> client.getIndexerOperation(indexerOperationId).toBlocking().single().content().toBlocking().single(),
						indexerOperation -> indexerOperation != null && indexerOperation.getStatus() != pendingStatus,
						INDEXER_OPERATION_RETRY_CONFIGURATION, indexerOperation -> new SnRuntimeException(errorMessage)));

		if (indexerOperationDTO.getStatus() != expectedStatus)
		{
			throw new SnRuntimeException(errorMessage);
		}

		return indexerOperationDTO;
	}

	protected IndexerOperationTypeDTO convertIndexerOperationType(final SnIndexerOperationType source)
	{
		return IndexerOperationTypeDTO.valueOf(source.name());
	}

	protected SnIndexerOperationType convertIndexerOperationType(final IndexerOperationTypeDTO source)
	{
		return SnIndexerOperationType.valueOf(source.name());
	}

	protected IndexerOperationStatusDTO convertIndexerOperationStatus(final SnIndexerOperationStatus source)
	{
		return IndexerOperationStatusDTO.valueOf(source.name());
	}

	protected SnIndexerOperationStatus convertIndexerOperationStatus(final IndexerOperationStatusDTO source)
	{
		return SnIndexerOperationStatus.valueOf(source.name());
	}

	protected SnIndexerOperation convertIndexerOperation(final IndexerOperationDTO source)
	{
		final SnIndexerOperation target = new SnIndexerOperation();
		target.setId(source.getId());
		target.setIndexTypeId(source.getIndexTypeId());
		target.setIndexId(source.getIndexId());
		target.setOperationType(convert(source.getOperationType(), this::convertIndexerOperationType));
		target.setStatus(convert(source.getStatus(), this::convertIndexerOperationStatus));

		return target;
	}

	@Override
	public SnDocumentBatchResponse executeDocumentBatch(final SnContext context, final String indexId,
			final SnDocumentBatchRequest documentBatchRequest, final String indexerOperationId) throws SnException
	{
		final CSSearchClient client = createClient(context.getIndexConfiguration());

		final AsyncTaskResponseDTO asyncTaskResponseDTO = handleRequestException(DOCUMENT_BATCH_FAILED_MSG, () -> {
			final DocumentBatchRequestDTO documentBatchRequestDTO = convertDocumenBatchRequest(documentBatchRequest,
					indexerOperationId);
			return client.executeDocumentBatch(indexId, documentBatchRequestDTO).toBlocking().single().content().toBlocking()
					.single();
		});

		final String taskId = asyncTaskResponseDTO.getTask().getId();

		final TaskDTO getTaskDTO = waitForTaskStatusOrElseThrow(client, taskId, TaskStatusDTO.PENDING, TaskStatusDTO.COMPLETED,
				DOCUMENT_BATCH_FAILED_MSG, DOCUMENT_BATCH_RETRY_CONFIGURATION);
		final DocumentBatchResponseDTO documentBatchResponseDTO = JsonUtils.fromJson(getTaskDTO.getData(),
				DocumentBatchResponseDTO.class);

		return convertDocumenBatchResponse(documentBatchResponseDTO);
	}

	protected DocumentDTO convertDocument(final SnDocument source)
	{
		return mapperFacade.map(source, DocumentDTO.class);
	}

	protected DocumentBatchRequestDTO convertDocumenBatchRequest(final SnDocumentBatchRequest source,
			final String indexerOperationId)
	{
		final DocumentBatchRequestDTO target = new DocumentBatchRequestDTO();
		target.setId(source.getId());
		target.setIndexerOperationId(indexerOperationId);
		target.setRequests(ConverterUtils.convertAll(source.getRequests(), this::convertDocumenBatchOperationRequest));

		return target;
	}

	protected DocumentBatchOperationRequestDTO convertDocumenBatchOperationRequest(final SnDocumentBatchOperationRequest source)
	{
		final DocumentBatchOperationRequestDTO target = new DocumentBatchOperationRequestDTO();
		target.setMethod(convertDocumenOperationType(source.getOperationType()));
		target.setId(source.getId());
		target.setBody(ConverterUtils.convert(source.getDocument(), this::convertDocument));

		return target;
	}

	protected String convertDocumenOperationType(final SnDocumentOperationType source)
	{
		if (SnDocumentOperationType.CREATE == source)
		{
			return "POST";
		}
		else if (SnDocumentOperationType.CREATE_UPDATE == source)
		{
			return "PUT";
		}
		else if (SnDocumentOperationType.PARTIAL_UPDATE == source)
		{
			return "PATCH";
		}
		else if (SnDocumentOperationType.DELETE == source)
		{
			return "DELETE";
		}
		else
		{
			throw new SnRuntimeException(MessageFormat.format("Cannot convert document operation type ''{0}''", source));
		}
	}

	protected SnDocumentBatchResponse convertDocumenBatchResponse(final DocumentBatchResponseDTO source)
	{
		final SnDocumentBatchResponse target = new SnDocumentBatchResponse();
		target.setResponses(ConverterUtils.convertAll(source.getResponses(), this::convertDocumenBatchOperationResponse));

		return target;
	}

	protected SnDocumentBatchOperationResponse convertDocumenBatchOperationResponse(final DocumentBatchOperationResponseDTO source)
	{
		final SnDocumentBatchOperationResponse target = new SnDocumentBatchOperationResponse();
		target.setId(source.getId());
		target.setStatus(convertDocumenOperationStatus(source.getStatusCode()));

		return target;
	}

	protected SnDocumentOperationStatus convertDocumenOperationStatus(final Integer statusCode)
	{
		final SnDocumentOperationStatus result;
		if (statusCode.intValue() == HttpStatus.CREATED.value())
		{
			result = SnDocumentOperationStatus.CREATED;
		}
		else if (statusCode.intValue() == HttpStatus.OK.value())
		{
			result = SnDocumentOperationStatus.UPDATED;
		}
		else if (statusCode.intValue() == HttpStatus.NO_CONTENT.value())
		{
			result = SnDocumentOperationStatus.DELETED;
		}
		else if (statusCode.intValue() == HttpStatus.INTERNAL_SERVER_ERROR.value())
		{
			result = SnDocumentOperationStatus.FAILED;
		}
		else
		{
			throw new SnRuntimeException(
					MessageFormat.format("Cannot convert document operation result for status code ''{0}''", statusCode));
		}
		return result;
	}

	@Override
	public void commit(final SnContext context, final String indexId) throws SnException
	{
		final CSSearchClient client = createClient(context.getIndexConfiguration());

		final AbstractIndexStateResponseDTO indexStateResponseDTO = handleRequestException(COMMIT_INDEX_FAILED_MSG, () -> {
			final CommitIndexStateRequestDTO indexStateRequest = new CommitIndexStateRequestDTO();
			return client.executeIndexStateRequest(indexId, indexStateRequest).toBlocking().single().content().toBlocking().single();
		});

		if (indexStateResponseDTO instanceof TaskIndexStateResponseDTO)
		{
			final TaskIndexStateResponseDTO taskIndexStateResponseDTO = (TaskIndexStateResponseDTO) indexStateResponseDTO;
			final String taskId = taskIndexStateResponseDTO.getTask().getId();

			waitForTaskStatusOrElseThrow(client, taskId, TaskStatusDTO.PENDING, TaskStatusDTO.COMPLETED, COMMIT_INDEX_FAILED_MSG,
					INDEX_COMMIT_RETRY_CONFIGURATION);
		}
	}

	protected TaskDTO waitForTaskStatusOrElseThrow(final CSSearchClient client, final String taskId,
			final TaskStatusDTO pendingStatus, final TaskStatusDTO expectedStatus, final String errorMessage,
			final RetryConfiguration retryConfiguration) throws SnException
	{
		final TaskDTO taskDTO = handleRequestException(errorMessage,
				() -> RetryUtils.waitForConditionOrElseThrow(
						() -> client.getTask(taskId).toBlocking().single().content().toBlocking().single(),
						task -> task != null && task.getStatus() != pendingStatus, retryConfiguration,
						task -> new SnRuntimeException(errorMessage)));

		if (taskDTO.getStatus() != expectedStatus)
		{
			throw new SnRuntimeException(errorMessage);
		}

		return taskDTO;
	}

	@Override
	public SnSearchResult search(final SnContext context, final String indexId, final SnSearchQuery searchQuery) throws SnException
	{
		final CSSearchClient client = createClient(context.getIndexConfiguration());

		final String languageHeader = createLanguageHeader(context);
		final String qualifierHeader = createQualifierHeader(context);
		final SearchQueryDTO searchQueryDTO = convertSearchQuery(searchQuery);

		final SearchResultDTO searchResultDTO = handleRequestException("Search query failed", () -> {
			final RawResponse<SearchResultDTO> response = client.search(languageHeader, qualifierHeader, indexId, searchQueryDTO)
					.toBlocking().single();
			return response.content().toBlocking().single();
		});

		return convertSearchResult(searchQuery, searchResultDTO);
	}

	protected SearchQueryDTO convertSearchQuery(final SnSearchQuery source)
	{
		return mapperFacade.map(source, SearchQueryDTO.class);
	}

	protected SnSearchResult convertSearchResult(final SnSearchQuery searchQuery, final SearchResultDTO source)
	{
		final SnSearchResult searchResult = mapperFacade.map(source, SnSearchResult.class);

		// added for backwards compatibility reasons
		searchResult.setOffset(searchQuery.getSkip());
		searchResult.setLimit(searchQuery.getTop());

		return searchResult;
	}

	@Override
	public SnSuggestResult suggest(final SnContext context, final String indexId, final SnSuggestQuery suggestQuery)
			throws SnException
	{
		final CSSearchClient client = createClient(context.getIndexConfiguration());

		final String languageHeader = createLanguageHeader(context);
		final String qualifierHeader = createQualifierHeader(context);
		final SuggestQueryDTO suggestQueryDTO = convertSuggestQuery(suggestQuery);

		final SuggestResultDTO suggestResultDTO = handleRequestException("Suggest query failed", () -> {
			final RawResponse<SuggestResultDTO> response = client.suggest(languageHeader, qualifierHeader, indexId, suggestQueryDTO)
					.toBlocking().single();
			return response.content().toBlocking().single();
		});

		return convertSuggestResult(suggestResultDTO);
	}

	protected SuggestQueryDTO convertSuggestQuery(final SnSuggestQuery source)
	{
		return mapperFacade.map(source, SuggestQueryDTO.class);
	}

	protected SnSuggestResult convertSuggestResult(final SuggestResultDTO source)
	{
		return mapperFacade.map(source, SnSuggestResult.class);
	}

	protected String createLanguageHeader(final SnContext context)
	{
		if (MapUtils.isEmpty(context.getQualifiers()))
		{
			return null;
		}

		final List<SnQualifier> languages = context.getQualifiers().get(SearchservicesConstants.LANGUAGE_QUALIFIER_TYPE);
		if (CollectionUtils.isEmpty(languages))
		{
			return null;
		}

		final Locale locale = commonI18NService.getLocaleForIsoCode(languages.get(0).getId());
		return locale.toLanguageTag();
	}

	protected String createQualifierHeader(final SnContext context)
	{
		if (MapUtils.isEmpty(context.getQualifiers()))
		{
			return null;
		}

		return context.getQualifiers().entrySet().stream().filter(this::filterQualifierHeader).map(this::buildQualifierHeader)
				.collect(Collectors.joining(","));
	}

	protected boolean filterQualifierHeader(final Entry<String, List<SnQualifier>> entry)
	{
		return entry.getKey() != null && !StringUtils.equals(entry.getKey(), SearchservicesConstants.LANGUAGE_QUALIFIER_TYPE)
				&& CollectionUtils.isNotEmpty(entry.getValue());
	}

	protected String buildQualifierHeader(final Entry<String, List<SnQualifier>> entry)
	{
		return entry.getKey() + "=" + entry.getValue().get(0).getId();
	}

	protected CSSearchClient createClient(final SnIndexConfiguration indexConfiguration) throws SnException
	{
		try
		{
			final CSSearchSnSearchProviderConfiguration searchProviderConfiguration = getSearchProviderConfiguration(
					indexConfiguration);

			final ConsumedDestinationModel destination = destinationService.getDestinationByIdAndByDestinationTargetId(
					searchProviderConfiguration.getDestinationId(), searchProviderConfiguration.getDestinationTargetId());

			final Map<String, String> clientConfig = apiRegistryClientService.buildClientConfig(CSSearchClient.class, destination);

			return clientFactory.client(clientFactory.buildCacheKey(destination), CSSearchClient.class, clientConfig);
		}
		catch (final CredentialException e)
		{
			throw new SnException("Failed to create client", e);
		}
		catch (final HttpException e)
		{
			throw new SnException(buildRequestExceptionMessage("Failed to create client", e));
		}
	}

	protected Map<Locale, String> buildLocalizedName(final ConverterContext converterContext, final Map<Locale, String> source)
	{
		if (source == null)
		{
			return null;
		}

		final Map<Locale, String> target = new LinkedHashMap<>();

		for (final Entry<Locale, String> entry : source.entrySet())
		{
			if (converterContext.getLocales().contains(entry.getKey()))
			{
				target.put(entry.getKey(), entry.getValue());
			}
		}

		return target;
	}

	protected void handleRequestException(final String message, final InterruptableRunnable handler) throws SnException
	{
		handleRequestException(message, () -> {
			handler.run();
			return null;
		});
	}

	protected <T> T handleRequestException(final String message, final InterruptableSupplier<T> handler) throws SnException
	{
		try
		{
			return handler.get();
		}
		catch (final HttpException e)
		{
			final String exceptionMessage = buildRequestExceptionMessage(message, e);
			LOG.warn(exceptionMessage);
			throw new SnException(exceptionMessage);
		}
		catch (final InterruptedException e)
		{
			LOG.warn(MessageFormat.format("{0}, reason: {1}", message, e.getClass().getSimpleName()));
			Thread.currentThread().interrupt();
			throw new SnException(message, e);
		}
	}

	protected String buildRequestExceptionMessage(final String message, final HttpException exception)
	{
		return MessageFormat.format("{0}, reason: code={1}, status={2}, message={3}", //
				message, //
				exception.getCode(), //
				exception.getStatus() != null ? exception.getStatus() : "", //
				exception.getServerMessage() != null ? exception.getServerMessage().toBlocking().first() : "");
	}

	public DestinationService<ConsumedDestinationModel> getDestinationService()
	{
		return destinationService;
	}

	@Required
	public void setDestinationService(final DestinationService<ConsumedDestinationModel> destinationService)
	{
		this.destinationService = destinationService;
	}

	public ApiRegistryClientService getApiRegistryClientService()
	{
		return apiRegistryClientService;
	}

	@Required
	public void setApiRegistryClientService(final ApiRegistryClientService apiRegistryClientService)
	{
		this.apiRegistryClientService = apiRegistryClientService;
	}

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	public ClientFactory getClientFactory()
	{
		return clientFactory;
	}

	@Required
	public void setClientFactory(final ClientFactory clientFactory)
	{
		this.clientFactory = clientFactory;
	}

	protected static class ConverterContext
	{
		private final Set<Locale> locales;

		public ConverterContext(final List<Locale> locales)
		{
			this.locales = Set.copyOf(locales);
		}

		public Set<Locale> getLocales()
		{
			return locales;
		}
	}

	public interface InterruptableRunnable
	{
		void run() throws InterruptedException;
	}

	public interface InterruptableSupplier<V>
	{
		V get() throws InterruptedException;
	}

	protected static class ListCustomMapper extends CustomMapper<List<Object>, List<Object>>
	{
		@Override
		public void mapAtoB(final List<Object> source, final List<Object> destination, final MappingContext context)
		{
			map(source, destination, context);
		}

		@Override
		public void mapBtoA(final List<Object> source, final List<Object> destination, final MappingContext context)
		{
			map(source, destination, context);
		}

		protected void map(final List<Object> source, final List<Object> destination, final MappingContext context)
		{
			final Type<Object> sourceElementType = context.getResolvedSourceType().getNestedType(0);
			final Type<Object> destinationElementType = context.getResolvedDestinationType().getNestedType(0);

			destination.clear();

			for (final Object sourceElement : source)
			{
				if (sourceElement == null)
				{
					destination.add(null);
				}
				else
				{
					final Object destinationElement = mapperFacade.map(sourceElement, sourceElementType, destinationElementType,
							context);
					destination.add(destinationElement);
				}
			}
		}
	}

	protected static class MapCustomMapper extends CustomMapper<Map<String, Object>, Map<String, Object>>
	{
		@Override
		public void mapAtoB(final Map<String, Object> source, final Map<String, Object> destination, final MappingContext context)
		{
			map(source, destination);
		}

		@Override
		public void mapBtoA(final Map<String, Object> source, final Map<String, Object> destination, final MappingContext context)
		{
			map(source, destination);
		}

		protected void map(final Map<String, Object> source, final Map<String, Object> destination)
		{
			destination.clear();
			destination.putAll(source);
		}
	}

	protected static class LocaleConverter extends BidirectionalConverter<Locale, String>
	{
		@Override
		public String convertTo(final Locale source, final Type<String> destinationType, final MappingContext context)
		{
			return source == null ? null : source.toLanguageTag();
		}

		@Override
		public Locale convertFrom(final String source, final Type<Locale> destinationType, final MappingContext context)
		{
			return source == null ? null : Locale.forLanguageTag(source);
		}
	}

	protected static class SortOrderConverter extends BidirectionalConverter<SnSortOrder, SortOrderDTO>
	{
		@Override
		public SnSortOrder convertFrom(final SortOrderDTO source, final Type<SnSortOrder> destinationType,
				final MappingContext context)
		{
			if (source == null)
			{
				return null;
			}

			return source == SortOrderDTO.ASC ? SnSortOrder.ASCENDING : SnSortOrder.DESCENDING;
		}

		@Override
		public SortOrderDTO convertTo(final SnSortOrder source, final Type<SortOrderDTO> destinationType,
				final MappingContext context)
		{
			if (source == null)
			{
				return null;
			}

			return source == SnSortOrder.ASCENDING ? SortOrderDTO.ASC : SortOrderDTO.DESC;
		}
	}
}
