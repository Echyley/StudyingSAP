/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.converters.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.merchandising.model.MerchSnDocumentContainer;
import com.hybris.merchandising.model.Product;


/**
 * Class populating product details to merch product from indexed document container
 */
public class MerchSnProductDetailsPopulator implements Populator<MerchSnDocumentContainer, Product>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MerchSnProductDetailsPopulator.class);

	@Override
	public void populate(final MerchSnDocumentContainer source, final Product target) throws ConversionException
	{
		target.setCategories(populateCategory(source));
		target.setPageUrl(populatePageUrl(source));
		target.setReportingGroup(getReportingGroup(source));
		target.setProductDetails(populateProductDetails(source));
		populatePrice(source, target);
	}

	protected void populatePrice(final MerchSnDocumentContainer source, final Product target)
	{
		Double price = null;
		final String fieldId = source.getMerchContext().getMerchPropertiesMapping().get(Product.PRICE);
		final String currency = source.getMerchConfig().getCurrency().getIsocode();
		final Object priceObject = source.getInputDocument().getFields().get(fieldId);
		if (priceObject instanceof Map)
		{
			try
			{
				final Map priceMap = (Map) priceObject;
				price = getDoubleValue(priceMap.get(currency));
			}
			catch (final NumberFormatException e)
			{
				LOGGER.warn("Unexpected value for price field ({})", fieldId);
			}
		}
		else
		{
			price = getDoubleValue(priceObject);
		}

		if (price != null)
		{
			target.getProductDetails().put(Product.PRICE, price);
		}
	}

	private Double getDoubleValue(final Object price) throws NumberFormatException
	{
		if (price instanceof Number)
		{
			return Double.valueOf(((Number) price).doubleValue());
		}
		else if (price instanceof String)
		{
			return Double.valueOf(((String) price));
		}

		return null;
	}

	/**
	 * Method populating product specific details.
	 *
	 * @param source Object representing indexed document and configuration being used.
	 * @return Map contains name value pair of product properties.
	 */
	protected Map<String, Object> populateProductDetails(final MerchSnDocumentContainer source)
	{
		// When we add additional mapping configuration to merchandising properties, then it doesn't require
		// any code change to consider the new mapping fields, so we use Map instead of POJO
		return source.getMerchContext().getProductDetailsFields().entrySet().stream()
		             .filter(Objects::nonNull)
		             .collect(Collectors.toMap(Entry::getKey, entryValue ->
		             {
			             final String fieldId = entryValue.getValue();
			             return Optional.ofNullable(source.getInputDocument().getFields().get(fieldId)).orElse("");
		             }));
	}

	/**
	 * Method populating list of product categories
	 *
	 * @param source Object representing indexed document and configuration being used.
	 * @return List of categories that the product is in.
	 */
	protected List<String> populateCategory(final MerchSnDocumentContainer source)
	{
		final String fieldId = source.getMerchContext().getMerchPropertiesMapping().get(Product.CATEGORIES_FIELD);
		final Object categories = source.getInputDocument().getFields().get(fieldId);
		if (categories instanceof String)
		{
			return Collections.singletonList((String) categories);
		}
		else if (categories instanceof List)
		{
			return (List<String>) categories;
		}
		LOGGER.warn("Unable to map categories. Returning empty list");
		return new ArrayList<>(0);
	}

	/**
	 * Method populating product page URL. It add baseCatalogPageUrl set in configuration to url field value.
	 *
	 * @param source Object representing indexed document and configuration being used.
	 * @return product page URL.
	 */
	protected String populatePageUrl(final MerchSnDocumentContainer source)
	{
		final String fieldId = source.getMerchContext().getMerchPropertiesMapping().get(Product.PAGE_URL);
		return StringUtils.join(source.getMerchConfig().getBaseCatalogPageUrl(),
				(String) source.getInputDocument().getFields().get(fieldId));
	}

	/**
	 * Method populating reporting group to use with the product.
	 * It uses rollup strategy field from merchandising configuration to populate proper value
	 *
	 * @param source Object representing indexed document and configuration being used.
	 * @return the reporting group to use for the product.
	 */
	protected String getReportingGroup(final MerchSnDocumentContainer source)
	{
		return (String) Optional.ofNullable(source.getInputDocument().getFields().get(source.getMerchConfig().getRollUpStrategyField()))
		                        .orElse(source.getInputDocument().getFields().get(source.getMerchContext().getMerchPropertiesMapping().get(Product.ID)));

	}
}
