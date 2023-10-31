/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.converters.populators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.searchservices.search.data.SnRangeBucketRequest;
import de.hybris.platform.searchservices.search.data.SnRangeBucketResponse;
import de.hybris.platform.searchservices.search.data.SnRangeBucketsFacetRequest;
import de.hybris.platform.searchservices.search.data.SnRangeBucketsFacetResponse;
import de.hybris.platform.searchservices.search.data.SnTermBucketResponse;
import de.hybris.platform.searchservices.search.data.SnTermBucketsFacetRequest;
import de.hybris.platform.searchservices.search.data.SnTermBucketsFacetResponse;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.merchandising.model.FacetValue;
import com.hybris.merchandising.model.ProductFacet;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MerchSnProductFacetPopulatorTest extends AbstractMerchSnPopulatorTest
{
	protected static String EN_CATEGORY_NAME_1 = "enCategory1";
	protected static String EN_CATEGORY_BUCKET_NAME_1 = "Category 1";
	protected static String EN_CATEGORY_NAME_2 = "enCategory2";
	protected static String PRICE_FACET_NAME = "Price";
	protected static String CATEGORY_FACET_NAME = "Categories";
	protected static String PRICE_BUCKET_ID_1 = "USD_10_20";
	protected static String PRICE_BUCKET_NAME_1 = "USD 10-20";
	protected static String PRICE_BUCKET_ID_2 = "USD_20_30";
	protected static String PRICE_BUCKET_NAME_2 = "USD 20-30";
	protected static String PRICE_BUCKET_ID_3 = "USD_30_40";
	protected static String PRICE_BUCKET_NAME_3 = "USD 30-40";
	protected static String STOCK_LEVEL_BUCKET_NAME = "In Stock";

	@Mock
	protected SnRangeBucketsFacetResponse priceFacetResponse;
	@Mock
	protected SnTermBucketsFacetResponse stockFacetResponse;
	@Mock
	protected SnTermBucketsFacetResponse categoryFacetResponse;
	@Mock
	protected SnRangeBucketsFacetRequest priceFacetRequest;
	@Mock
	protected SnTermBucketsFacetRequest stockFacetRequest;
	@Mock
	protected SnTermBucketsFacetRequest categoryFacetRequest;

	protected SnRangeBucketRequest priceBucketRequest1 = createRangeBucketRequest(PRICE_BUCKET_ID_1, Double.valueOf(10), Double.valueOf(20));
	protected SnRangeBucketRequest priceBucketRequest2 = createRangeBucketRequest(PRICE_BUCKET_ID_2, "20", "30");
	protected SnRangeBucketRequest priceBucketRequest3 = createRangeBucketRequest(PRICE_BUCKET_ID_3, Integer.valueOf(30), Integer.valueOf(40));
	protected SnRangeBucketResponse priceBucketResponse1 = createRangeBucketResponse(PRICE_BUCKET_ID_1, PRICE_BUCKET_NAME_1);
	protected SnRangeBucketResponse priceBucketResponse2 = createRangeBucketResponse(PRICE_BUCKET_ID_2, PRICE_BUCKET_NAME_2);
	protected SnRangeBucketResponse priceBucketResponse3 = createRangeBucketResponse(PRICE_BUCKET_ID_3, PRICE_BUCKET_NAME_3);
	protected SnTermBucketResponse stockLevelBucketResponse = createTermBucketResponse(STOCK_LEVEL_VALUE, STOCK_LEVEL_BUCKET_NAME);
	protected SnTermBucketResponse categoryBucketResponse1 = createTermBucketResponse(EN_CATEGORY_NAME_1,
			EN_CATEGORY_BUCKET_NAME_1);
	protected SnTermBucketResponse categoryBucketResponse2 = createTermBucketResponse(EN_CATEGORY_NAME_2, null);

	protected MerchSnProductFacetPopulator populator;

	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		populator = new MerchSnProductFacetPopulator();

		when(priceFacetResponse.getId()).thenReturn(PRICE_FIELD);
		when(priceFacetResponse.getName()).thenReturn(PRICE_FACET_NAME);
		when(priceFacetRequest.getBuckets()).thenReturn(List.of(priceBucketRequest1, priceBucketRequest2, priceBucketRequest3));
		when(priceFacetResponse.getBuckets()).thenReturn(List.of(priceBucketResponse1, priceBucketResponse2, priceBucketResponse3));

		when(stockFacetResponse.getId()).thenReturn(STOCK_LEVEL_STATUS);
		when(stockFacetResponse.getBuckets()).thenReturn(List.of(stockLevelBucketResponse));

		when(categoryFacetResponse.getId()).thenReturn(CATEGORY_NAMES);
		when(categoryFacetResponse.getName()).thenReturn(CATEGORY_FACET_NAME);
		when(categoryFacetResponse.getBuckets()).thenReturn(List.of(categoryBucketResponse1, categoryBucketResponse2));

		when(merchContext.getFacetResponses()).thenReturn(
				Map.of(PRICE_FIELD, priceFacetResponse, STOCK_LEVEL_STATUS, stockFacetResponse, CATEGORY_NAMES, categoryFacetResponse));
		when(merchContext.getFacetRequests()).thenReturn(
				Map.of(PRICE_FIELD, priceFacetRequest, STOCK_LEVEL_STATUS, stockFacetRequest, CATEGORY_NAMES, categoryFacetRequest));

	}

	@Test
	public void testPopulate()
	{
		//given
		final Map categoryNames = Map.of(DEFAULT_LANGUAGE_ISO, List.of(EN_CATEGORY_NAME_1, EN_CATEGORY_NAME_2), DE, List.of("deCategory1", "deCategory2"));
		when(snDocument.getFields()).thenReturn(Map.of(PRICE_FIELD, PRICE_MAP_VALUE, STOCK_LEVEL_STATUS, STOCK_LEVEL_VALUE, CATEGORY_NAMES, categoryNames));

		//when
		populator.populate(source, target);

		//then
		final List<ProductFacet> productFacets = target.getFacets();
		assertNotNull(productFacets);
		assertEquals(3, productFacets.size());

		verifyPriceFacet(productFacets);
		verifyStatusLevelFacet(productFacets);
		verifyCategoryFacet(productFacets);
	}

	@Test
	public void testPopulateWhenNoValues()
	{
		//given
		when(snDocument.getFields()).thenReturn(Map.of());

		//when
		populator.populate(source, target);

		//then
		final List<ProductFacet> productFacets = target.getFacets();
		assertNotNull(productFacets);
		assertEquals(3, productFacets.size());
		productFacets.stream().forEach(f -> assertEquals(0, f.getValues().size()));
	}

	protected void verifyPriceFacet(final List<ProductFacet> productFacets)
	{
		final ProductFacet facet = findFacet(PRICE_FIELD, productFacets);
		assertEquals(PRICE_FACET_NAME, facet.getName());
		final List<FacetValue> values = facet.getValues();
		assertNotNull(values);
		assertEquals(1, values.size());
		final FacetValue value = values.get(0);
		assertEquals(PRICE_BUCKET_ID_2, value.getId());
		assertEquals(PRICE_BUCKET_NAME_2, value.getName());
	}

	protected void verifyStatusLevelFacet(final List<ProductFacet> productFacets)
	{
		final ProductFacet facet = findFacet(STOCK_LEVEL_STATUS, productFacets);
		assertEquals(STOCK_LEVEL_STATUS, facet.getName());
		final List<FacetValue> values = facet.getValues();
		assertNotNull(values);
		assertEquals(1, values.size());
		final FacetValue value = values.get(0);
		assertEquals(STOCK_LEVEL_VALUE, value.getId());
		assertEquals(STOCK_LEVEL_BUCKET_NAME, value.getName());
	}

	protected void verifyCategoryFacet(final List<ProductFacet> productFacets)
	{
		final ProductFacet facet = findFacet(CATEGORY_NAMES, productFacets);
		assertEquals(CATEGORY_FACET_NAME, facet.getName());
		final List<FacetValue> values = facet.getValues();
		assertNotNull(values);
		assertEquals(2, values.size());
		values.stream().forEach(v -> {
			if (EN_CATEGORY_NAME_1.equals(v.getId()))
			{
				assertEquals(EN_CATEGORY_BUCKET_NAME_1, v.getName());
			}
			else if (EN_CATEGORY_NAME_2.equals(v.getId()))
			{
				assertEquals(EN_CATEGORY_NAME_2, v.getName());
			}
			else
			{
				throw new AssertionError("Incorrect facet category value : " + v.getId());
			}
		});
	}

	protected ProductFacet findFacet(final String facetId, final List<ProductFacet> facets)
	{
		return facets.stream().filter(f -> facetId.equals(f.getId())).findFirst().orElse(null);
	}

	protected SnRangeBucketRequest createRangeBucketRequest(final String id, final Object from, final Object to)
	{
		final SnRangeBucketRequest bucket = new SnRangeBucketRequest();
		bucket.setId(id);
		bucket.setFrom(from);
		bucket.setTo(to);
		return bucket;
	}

	protected SnRangeBucketResponse createRangeBucketResponse(final String id, final String name)
	{
		final SnRangeBucketResponse bucket = new SnRangeBucketResponse();
		bucket.setId(id);
		bucket.setName(name);
		return bucket;
	}

	protected SnTermBucketResponse createTermBucketResponse(final String id, final String name)
	{
		final SnTermBucketResponse bucket = new SnTermBucketResponse();
		bucket.setId(id);
		bucket.setName(name);
		return bucket;
	}
}
