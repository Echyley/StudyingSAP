/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.converters.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.searchservices.search.data.AbstractSnBucketRequest;
import de.hybris.platform.searchservices.search.data.AbstractSnBucketResponse;
import de.hybris.platform.searchservices.search.data.AbstractSnFacetRequest;
import de.hybris.platform.searchservices.search.data.AbstractSnFacetResponse;
import de.hybris.platform.searchservices.search.data.SnRangeBucketRequest;
import de.hybris.platform.searchservices.search.data.SnRangeBucketsFacetRequest;
import de.hybris.platform.searchservices.search.data.SnRangeBucketsFacetResponse;
import de.hybris.platform.searchservices.search.data.SnTermBucketsFacetResponse;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.hybris.merchandising.model.FacetValue;
import com.hybris.merchandising.model.MerchSnDocumentContainer;
import com.hybris.merchandising.model.Product;
import com.hybris.merchandising.model.ProductFacet;


/**
 * Class populating product facets to merch product from indexed document container
 */
public class MerchSnProductFacetPopulator implements Populator<MerchSnDocumentContainer, Product>
{
	@Override
	public void populate(final MerchSnDocumentContainer source, final Product target) throws ConversionException
	{
		final List<ProductFacet> facetFields = source.getMerchContext().getFacetResponses().entrySet().stream()
		                                             .map(entry ->
		                                             {
			                                             final AbstractSnFacetResponse facetResponse = entry.getValue();
			                                             final AbstractSnFacetRequest facetRequest = source.getMerchContext().getFacetRequests().get(entry.getKey());

			                                             final ProductFacet facet = new ProductFacet();
			                                             facet.setId(entry.getKey());
			                                             facet.setName(StringUtils.isEmpty(facetResponse.getName()) ? facet.getId() : facetResponse.getName());
			                                             final List<FacetValue> facetValues = createMerchFacetValues(facetResponse, facetRequest, source);
			                                             facet.setValues(facetValues);

			                                             return facet;
		                                             })
		                                             .collect(Collectors.toList());

		target.setFacets(facetFields);
	}

	protected List<FacetValue> createMerchFacetValues(final AbstractSnFacetResponse facetResponse, final AbstractSnFacetRequest facetRequest,
	                                                  final MerchSnDocumentContainer source)
	{
		final List<FacetValue> facetValues = Lists.newArrayList();

		Object fieldValue = source.getInputDocument().getFields().get(facetResponse.getId());

		if (fieldValue instanceof Map)
		{
			fieldValue = getFacetValueFromMap((Map) fieldValue, source);
		}

		if (fieldValue instanceof Collection)
		{
			final Collection<Object> fieldValues = (Collection<Object>) fieldValue;

			fieldValues.stream()
			           .map(value -> createFacetValue(value, facetResponse, facetRequest))
			           .forEach(facetValues::add);
		}
		else if (fieldValue != null)
		{
			final FacetValue fv = createFacetValue(fieldValue, facetResponse, facetRequest);
			if (fv != null)
			{
				facetValues.add(fv);
			}
		}

		return facetValues;
	}

	private Object getFacetValueFromMap(final Map mapValue, final MerchSnDocumentContainer source)
	{
		Object value = getLocalizedValue(mapValue, source);

		if (value == null)
		{
			value = getCurrencyValue(mapValue, source);
		}

		return value;
	}

	private Object getLocalizedValue(final Map mapValue, final MerchSnDocumentContainer source)
	{
		Object value = mapValue.get(source.getMerchContext().getDefaultLocale());
		if (value == null)
		{
			value = mapValue.get(source.getMerchConfig().getDefaultLanguage());
		}
		if (value == null)
		{
			value = mapValue.get(source.getMerchConfig().getDefaultLanguage().getIsocode());
		}
		return value;
	}

	private Object getCurrencyValue(final Map mapValue, final MerchSnDocumentContainer source)
	{
		return mapValue.get(source.getMerchConfig().getCurrency().getIsocode());
	}

	protected FacetValue createFacetValue(final Object fieldValue, final AbstractSnFacetResponse facetResponse, final AbstractSnFacetRequest facetRequest)
	{
		final FacetValue facetValue = new FacetValue();

		final Object value = mapFieldValue(fieldValue, facetRequest);
		if (value == null)
		{
			return null;
		}

		final String valueDisplayName = getValueDisplayName(value, facetResponse);
		facetValue.setId(value);
		facetValue.setName(valueDisplayName);

		return facetValue;
	}

	private Object mapFieldValue(final Object fieldValue, final AbstractSnFacetRequest facetRequestData)
	{
		if (facetRequestData instanceof SnRangeBucketsFacetRequest)
		{
			final SnRangeBucketsFacetRequest rangeFacetRequest = (SnRangeBucketsFacetRequest) facetRequestData;
			return rangeFacetRequest.getBuckets().stream()
			                        .filter(b -> belongToBucket(b, fieldValue, rangeFacetRequest))
			                        .map(AbstractSnBucketRequest::getId)
			                        .findFirst().orElse(null);

		}

		return fieldValue;
	}

	private boolean belongToBucket(final SnRangeBucketRequest b, final Object fieldValue, final SnRangeBucketsFacetRequest rangeFacetRequest)
	{
		if (fieldValue instanceof Number)
		{
			try
			{
				final double value = ((Number) fieldValue).doubleValue();
				final double from = Double.parseDouble(b.getFrom().toString());
				final double to = Double.parseDouble(b.getTo().toString());
				return (Boolean.TRUE.equals(rangeFacetRequest.getIncludeFrom()) ? value >= from : value > from)
						&& (Boolean.TRUE.equals(rangeFacetRequest.getIncludeTo()) ? value <= to : value < to);
			}
			catch (final NumberFormatException e)
			{
				return false;
			}
		}
		else if (fieldValue instanceof String)
		{
			final String value = (String) fieldValue;
			final String from = b.getFrom().toString();
			final String to = b.getTo().toString();
			return (Boolean.TRUE.equals(rangeFacetRequest.getIncludeFrom()) ? value.compareTo(from) >= 0 : value.compareTo(from) > 0)
					&& (Boolean.TRUE.equals(rangeFacetRequest.getIncludeTo()) ? value.compareTo(to) <= 0 : value.compareTo(to) < 0);
		}

		return false;
	}

	private String getValueDisplayName(final Object fieldValue, final AbstractSnFacetResponse facetData)
	{
		final String stringValue = fieldValue.toString();
		if (facetData instanceof SnRangeBucketsFacetResponse)
		{
			final SnRangeBucketsFacetResponse facet = (SnRangeBucketsFacetResponse) facetData;

			return facet.getBuckets().stream()
			            .filter(b -> StringUtils.equals(stringValue, b.getId()))
			            .findFirst()
			            .map(AbstractSnBucketResponse::getName)
			            .orElse(stringValue);
		}
		else if (facetData instanceof SnTermBucketsFacetResponse)
		{
			final SnTermBucketsFacetResponse facet = (SnTermBucketsFacetResponse) facetData;

			return facet.getBuckets().stream()
			            .filter(b -> StringUtils.equals(stringValue, b.getId()))
			            .findFirst()
			            .map(AbstractSnBucketResponse::getName)
			            .orElse(stringValue);
		}
		return stringValue;
	}
}
