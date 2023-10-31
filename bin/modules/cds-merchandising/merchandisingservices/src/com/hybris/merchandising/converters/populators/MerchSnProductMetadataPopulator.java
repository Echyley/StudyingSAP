/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.converters.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.merchandising.model.MerchSnDocumentContainer;
import com.hybris.merchandising.model.Product;
import com.hybris.merchandising.model.ProductMetadata;


/**
 * Class populating merch product metadata (name, summary, description)
 */
public class MerchSnProductMetadataPopulator implements Populator<MerchSnDocumentContainer, Product>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MerchSnProductMetadataPopulator.class);

	@Override
	public void populate(final MerchSnDocumentContainer source, final Product target) throws ConversionException
	{
		final Map<String, ProductMetadata> metadata = source.getMerchContext().getLocales().entrySet()
		                                                    .stream()
		                                                    .map(lang -> Pair.of(lang.getKey().getIsocode(), buildMetaData(source, lang)))
		                                                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

		target.setMetadata(metadata);
	}

	protected ProductMetadata buildMetaData(final MerchSnDocumentContainer source, final Map.Entry<LanguageModel, Locale> locale)
	{
		final ProductMetadata productMetadata = new ProductMetadata();

		Optional.ofNullable(extractFieldValue(source, locale, Product.NAME))
		        .ifPresent(name -> productMetadata.setName(sanitiseField(name)));
		Optional.ofNullable(extractFieldValue(source, locale, Product.SUMMARY))
		        .ifPresent(summary -> productMetadata.setSummary(sanitiseField(summary)));
		Optional.ofNullable(extractFieldValue(source, locale, Product.DESCRIPTION))
		        .ifPresent(description -> productMetadata.setDescription(sanitiseField(description)));

		return productMetadata;
	}

	protected String extractFieldValue(final MerchSnDocumentContainer source, final Map.Entry<LanguageModel, Locale> locale, final String merchFieldId)
	{
		final String indexedFieldId = source.getMerchContext().getMerchPropertiesMapping().get(merchFieldId);
		final Object fieldValue = source.getInputDocument().getFields().get(indexedFieldId);
		if (fieldValue instanceof Map)
		{
			final Map valueMap = (Map) fieldValue;
			return Optional.ofNullable(getLocalizedValue(valueMap, locale))
			               .orElse(StringUtils.EMPTY);
		}
		return StringUtils.EMPTY;
	}

	private String getLocalizedValue(final Map mapValue, final Map.Entry<LanguageModel, Locale> locale)
	{
		Object value = mapValue.get(locale.getValue());
		if (value == null)
		{
			value = mapValue.get(locale.getKey());
		}
		if (value == null)
		{
			value = mapValue.get(locale.getKey().getIsocode());
		}

		return value == null ? null : value.toString();
	}

	private String sanitiseField(final String field)
	{
		if (StringUtils.isEmpty(field))
		{
			return field;
		}
		try
		{
			return URLEncoder.encode(field, StandardCharsets.UTF_8.name());
		}
		catch (final UnsupportedEncodingException e)
		{
			LOGGER.error("Unable to URL encode field", e);
			return field;
		}
	}
}
