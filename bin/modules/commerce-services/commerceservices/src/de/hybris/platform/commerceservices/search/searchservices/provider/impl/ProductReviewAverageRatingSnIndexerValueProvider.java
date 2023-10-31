/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.search.searchservices.provider.impl;

import static java.util.Map.entry;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.searchservices.enums.SnDocumentOperationType;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSourceOperation;
import de.hybris.platform.searchservices.indexer.service.SnIndexerValueProvider;
import de.hybris.platform.searchservices.indexer.service.impl.AbstractSnIndexerValueProvider;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation of {@link SnIndexerValueProvider} for average product review rating.
 */
public class ProductReviewAverageRatingSnIndexerValueProvider extends
		AbstractSnIndexerValueProvider<ProductModel, ProductReviewAverageRatingSnIndexerValueProvider.ProductReviewAverageRatingData>
{
	public static final String ID = "productReviewAverageRatingSnIndexerValueProvider";

	protected static final Set<Class<?>> SUPPORTED_QUALIFIER_CLASSES = Set.of(LanguageModel.class);

	protected static final String DATA_KEY = ID + ".data";

	private static final String PARAM_PRODUCT_PKS = "productPks";
	private static final String PARAM_APPROVAL_STATUS = "approvalStatus";
	private static final String PARAM_LANGUAGE_PKS = "languagePks";

	private static final String AVG_CUSTOMER_RATINGS_SELECT = "SELECT {cr." + CustomerReviewModel.PRODUCT + "}, avg({cr."
			+ CustomerReviewModel.RATING + "}) ";
	private static final String AVG_CUSTOMER_RATINGS_FROM = "FROM {" + CustomerReviewModel._TYPECODE + " AS cr} ";
	private static final String AVG_CUSTOMER_RATINGS_WHERE = "WHERE {cr." + CustomerReviewModel.APPROVALSTATUS + "} = ?"
			+ PARAM_APPROVAL_STATUS + " AND {cr." + CustomerReviewModel.RATING + "} IS NOT NULL AND {cr."
			+ CustomerReviewModel.PRODUCT + "} IN (?" + PARAM_PRODUCT_PKS + ") ";
	private static final String AVG_CUSTOMER_RATINGS_GROUP_BY = "GROUP BY {cr." + CustomerReviewModel.PRODUCT + "} ";

	private static final String AVG_CUSTOMER_RATINGS_QUERY = AVG_CUSTOMER_RATINGS_SELECT + AVG_CUSTOMER_RATINGS_FROM
			+ AVG_CUSTOMER_RATINGS_WHERE + AVG_CUSTOMER_RATINGS_GROUP_BY;

	private static final String AVG_LOCALIZED_CUSTOMER_RATINGS_QUERY = AVG_CUSTOMER_RATINGS_SELECT + ", {cr."
			+ CustomerReviewModel.LANGUAGE + "} " + AVG_CUSTOMER_RATINGS_FROM + AVG_CUSTOMER_RATINGS_WHERE + "AND {cr."
			+ CustomerReviewModel.LANGUAGE + "} IN (?" + PARAM_LANGUAGE_PKS + ") " + AVG_CUSTOMER_RATINGS_GROUP_BY + ", {cr."
			+ CustomerReviewModel.LANGUAGE + "}";

	private FlexibleSearchService flexibleSearchService;

	private CommonI18NService commonI18NService;

	@Override
	public Set<Class<?>> getSupportedQualifierClasses() throws SnIndexerException
	{
		return SUPPORTED_QUALIFIER_CLASSES;
	}

	@Override
	protected ProductReviewAverageRatingData loadData(final SnIndexerContext indexerContext,
			final Collection<SnIndexerFieldWrapper> fieldWrappers, final ProductModel source) throws SnIndexerException
	{
		ProductReviewAverageRatingData ratingData = (ProductReviewAverageRatingData) indexerContext.getAttributes().get(DATA_KEY);
		if (ratingData == null)
		{
			ratingData = loadProductReviewAverageRatingData(indexerContext, fieldWrappers);
			indexerContext.getAttributes().put(DATA_KEY, ratingData);
		}
		return ratingData;
	}

	protected ProductReviewAverageRatingData loadProductReviewAverageRatingData(final SnIndexerContext indexerContext,
			final Collection<SnIndexerFieldWrapper> fieldWrappers) throws SnIndexerException
	{
		Map<PK, Double> averageRatings = null;
		Map<PK, Map<Locale, Double>> localizedAverageRatings = null;

		final Set<PK> productPks = getProductPksFromIndexerContext(indexerContext);
		if (CollectionUtils.isNotEmpty(productPks))
		{
			if (fieldWrappers.stream().anyMatch(Predicate.not(SnIndexerFieldWrapper::isLocalized)))
			{
				averageRatings = findProductRatingsByProductPks(productPks);
			}
			if (fieldWrappers.stream().anyMatch(SnIndexerFieldWrapper::isLocalized))
			{
				final Set<PK> languagePks = fieldWrappers.stream().flatMap(fw -> fw.getQualifiers().stream())
						.map(q -> q.getAs(LanguageModel.class)).map(LanguageModel::getPk).collect(Collectors.toSet());
				localizedAverageRatings = findLocalizedProductRatingsByProductPks(productPks, languagePks);
			}
		}
		return new ProductReviewAverageRatingData(averageRatings, localizedAverageRatings);
	}

	@Override
	protected Object getFieldValue(final SnIndexerContext indexerContext, final SnIndexerFieldWrapper fieldWrapper,
			final ProductModel source, final ProductReviewAverageRatingData averageRatingData) throws SnIndexerException
	{
		if (fieldWrapper.isLocalized())
		{
			return averageRatingData.getLocalizedAverageRatings().get(source.getPk());
		}
		return averageRatingData.getAverageRatings().get(source.getPk());
	}

	protected Set<PK> getProductPksFromIndexerContext(final SnIndexerContext indexerContext) throws SnIndexerException
	{
		if (indexerContext.getIndexerItemSourceOperations() == null)
		{
			return Collections.emptySet();
		}
		final Set<PK> pks = new HashSet<>();
		for (final SnIndexerItemSourceOperation itemSourceOperation : indexerContext.getIndexerItemSourceOperations())
		{
			if (itemSourceOperation.getDocumentOperationType() != SnDocumentOperationType.DELETE
					&& itemSourceOperation.getIndexerItemSource() != null)
			{
				pks.addAll(itemSourceOperation.getIndexerItemSource().getPks(indexerContext));
			}
		}
		return pks;
	}

	protected Map<PK, Double> findProductRatingsByProductPks(final Set<PK> productPks)
	{
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(AVG_CUSTOMER_RATINGS_QUERY, Map
				.ofEntries(entry(PARAM_PRODUCT_PKS, productPks), entry(PARAM_APPROVAL_STATUS, CustomerReviewApprovalType.APPROVED)));
		searchQuery.setResultClassList(Arrays.asList(PK.class, Double.class));

		final SearchResult<List<Object>> searchResult = flexibleSearchService.search(searchQuery);
		final int searchResultCount = searchResult.getCount();
		if (searchResultCount == 0)
		{
			return Collections.emptyMap();
		}
		return searchResult.getResult().stream().collect(Collectors.toMap(v -> (PK) v.get(0), v -> (Double) v.get(1)));
	}

	protected Map<PK, Map<Locale, Double>> findLocalizedProductRatingsByProductPks(final Set<PK> productPks,
			final Set<PK> languagePks)
	{
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(AVG_LOCALIZED_CUSTOMER_RATINGS_QUERY,
				Map.ofEntries(entry(PARAM_PRODUCT_PKS, productPks), entry(PARAM_APPROVAL_STATUS, CustomerReviewApprovalType.APPROVED),
						entry(PARAM_LANGUAGE_PKS, languagePks)));
		searchQuery.setResultClassList(Arrays.asList(PK.class, Double.class, LanguageModel.class));

		final SearchResult<List<Object>> searchResult = flexibleSearchService.search(searchQuery);
		final int searchResultCount = searchResult.getCount();

		if (searchResultCount == 0)
		{
			return Collections.emptyMap();
		}

		final int columnPositionProduct = 0;
		final int columnPositionRating = 1;
		final int columnPositionLanguage = 2;
		return searchResult.getResult().stream()
				.collect(Collectors.groupingBy(v -> (PK) v.get(columnPositionProduct),
						Collectors.toMap(v -> commonI18NService.getLocaleForLanguage((LanguageModel) v.get(columnPositionLanguage)),
								v -> (Double) v.get(columnPositionRating))));
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected static class ProductReviewAverageRatingData
	{
		private final Map<PK, Map<Locale, Double>> localizedAverageRatings;
		private final Map<PK, Double> averageRatings;

		public ProductReviewAverageRatingData(final Map<PK, Double> averageRatings,
				final Map<PK, Map<Locale, Double>> localizedAverageRatings)
		{
			this.localizedAverageRatings = localizedAverageRatings;
			this.averageRatings = averageRatings;
		}

		public Map<PK, Map<Locale, Double>> getLocalizedAverageRatings()
		{
			return localizedAverageRatings == null ? Collections.emptyMap() : localizedAverageRatings;
		}

		public Map<PK, Double> getAverageRatings()
		{
			return averageRatings == null ? Collections.emptyMap() : averageRatings;
		}
	}
}
