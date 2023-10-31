/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.model;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.searchservices.search.data.AbstractSnFacetRequest;
import de.hybris.platform.searchservices.search.data.AbstractSnFacetResponse;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


/**
 * Merchandising indexer context containing information required to synchronize products to catalog service
 */
public class MerchSnSynchContext
{
	/**
	 * Map of (merch_field_id , indexed_field_id)
	 */
	private Map<String, String> merchPropertiesMapping = Map.of();
	/**
	 * Map of merch fields (merch_field_id , indexed_field_id) which will be populated by general method as product details
	 */
	private Map<String, String> productDetailsFields = Map.of();
	/**
	 * Information about facets returned by search service query
	 */
	private Map<String, AbstractSnFacetResponse> facetResponses = Map.of();
	/**
	 * Information about facets
	 */
	private Map<String, AbstractSnFacetRequest> facetRequests = Map.of();

	/**
	 * Default locale used for localized fields
	 */
	private Locale defaultLocale;

	/**
	 * Map containing languages and Locale objects for localized fields
	 */
	private Map<LanguageModel, Locale> locales;

	/**
	 * Number of synchronized products
	 */
	private AtomicLong numberOfProducts = new AtomicLong(0);

	public static MerchSnSynchContext from(final Map<String, String> merchPropertiesMapping, final Locale defaultLocale, final Map<LanguageModel, Locale> locales)
	{
		final MerchSnSynchContext context = new MerchSnSynchContext();
		context.setMerchPropertiesMapping(merchPropertiesMapping);
		context.defaultLocale = defaultLocale;
		context.setLocales(locales);
		return context;
	}

	public static MerchSnSynchContext from(final Map<String, String> merchPropertiesMapping, final Map<String, AbstractSnFacetRequest> facetRequests,
	                                       final Map<String, AbstractSnFacetResponse> facetResponses, final Locale defaultLocale)
	{
		final MerchSnSynchContext context = new MerchSnSynchContext();
		context.setMerchPropertiesMapping(merchPropertiesMapping);
		context.facetRequests = facetRequests;
		context.facetResponses = facetResponses;
		context.defaultLocale = defaultLocale;
		return context;
	}

	public Map<String, String> getMerchPropertiesMapping()
	{
		return merchPropertiesMapping;
	}

	public void setMerchPropertiesMapping(final Map<String, String> merchPropertiesMapping)
	{
		this.merchPropertiesMapping = merchPropertiesMapping;
		if (merchPropertiesMapping != null)
		{
			productDetailsFields = merchPropertiesMapping.entrySet().stream()
			                                             .filter(m -> {
				                                             switch (m.getKey())
				                                             {
					                                             case Product.NAME:
					                                             case Product.DESCRIPTION:
					                                             case Product.SUMMARY:
					                                             case Product.CATEGORIES_FIELD:
					                                             case Product.PAGE_URL:
					                                             case Product.PRICE:
					                                             case Product.MAIN_IMAGE:
					                                             case Product.THUMBNAIL_IMAGE:
						                                             return false;
					                                             default:
						                                             return true;
				                                             }
			                                             })
			                                             .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}

	public Map<String, AbstractSnFacetResponse> getFacetResponses()
	{
		return facetResponses;
	}

	public void setFacetResponses(final Map<String, AbstractSnFacetResponse> facetResponses)
	{
		this.facetResponses = facetResponses;
	}

	public void setProductDetailsFields(final Map<String, String> productDetailsFields)
	{
		this.productDetailsFields = productDetailsFields;
	}

	public Map<String, String> getProductDetailsFields()
	{
		return productDetailsFields;
	}

	public Map<String, AbstractSnFacetRequest> getFacetRequests()
	{
		return facetRequests;
	}

	public void setFacetRequests(final Map<String, AbstractSnFacetRequest> facetRequests)
	{
		this.facetRequests = facetRequests;
	}

	public long getNumberOfProducts()
	{
		return numberOfProducts.get();
	}

	public void setNumberOfProducts(final AtomicLong numberOfProducts)
	{
		this.numberOfProducts = numberOfProducts;
	}

	public void addProducts(final long number)
	{
		numberOfProducts.addAndGet(number);
	}

	public Locale getDefaultLocale()
	{
		return defaultLocale;
	}

	public void setDefaultLocale(final Locale defaultLocale)
	{
		this.defaultLocale = defaultLocale;
	}

	public Map<LanguageModel, Locale> getLocales()
	{
		return locales;
	}

	public void setLocales(final Map<LanguageModel, Locale> locales)
	{
		this.locales = locales;
	}
}
