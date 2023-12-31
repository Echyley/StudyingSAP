/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.strategies.mappers.impl;


import de.hybris.platform.ruleengineservices.rule.strategies.RuleParameterValueMapper;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ProductConfigMessageRuleParameterValueMapper implements RuleParameterValueMapper<Map<Locale, String>>
{

	private static final String BACKSLASHES = "\\";
	private static final String DOUBLE_BACKSLASHES = "\\\\";
	protected static final String QUOTATION = "\"";
	protected static final String SERIALIZATION_QUOTATION = "@ser_quot@";
	protected static final String TEXT_QUOTATION = "@txt_quot@";
	private ObjectMapper objectMapper;

	@Override
	public String toString(final Map<Locale, String> messageMap)
	{
		String messageParameterAsString = null;
		ServicesUtil.validateParameterNotNull(messageMap, "Object cannot be null");
		try
		{
			final List<Locale> toBeRemoved = new ArrayList();
			for (final Map.Entry<Locale, String> entry : messageMap.entrySet())
			{
				final String value = entry.getValue();
				if (value != null)
				{
					final String finalValue = value.replace(BACKSLASHES, DOUBLE_BACKSLASHES).replace(QUOTATION, TEXT_QUOTATION);
					entry.setValue(finalValue);
				}
				else
				{
					toBeRemoved.add(entry.getKey());
				}
			}
			for (final Locale entryToBeRemoved : toBeRemoved)
			{
				messageMap.remove(entryToBeRemoved);
			}
			messageParameterAsString = getObjectMapper().writeValueAsString(messageMap);
			messageParameterAsString = messageParameterAsString.replace(QUOTATION, SERIALIZATION_QUOTATION);
		}
		catch (final JsonProcessingException e)
		{
			throw new IllegalArgumentException("Wrong localized message map", e);
		}
		return messageParameterAsString;
	}

	@Override
	public Map<Locale, String> fromString(final String value)
	{

		Map<Locale, String> result = new HashMap<>();
		try
		{
			if (value != null)
			{
				final String valueDecoded = value.replace(SERIALIZATION_QUOTATION, QUOTATION);
				result = getObjectMapper().readValue(valueDecoded, new TypeReference<Map<Locale, String>>()
				{});

				for (final Map.Entry<Locale, String> entry : result.entrySet())
				{
					final String entryValue = entry.getValue();
					final String newEntryValue = entryValue.replace(TEXT_QUOTATION, QUOTATION);
					entry.setValue(newEntryValue);
				}
				// -------------------------
			}
		}
		catch (final IOException e)
		{
			throw new IllegalArgumentException("Wrong localized message", e);
		}
		return result;
	}

	protected ObjectMapper getObjectMapper()
	{
		if (objectMapper == null)
		{
			objectMapper = new ObjectMapper();
		}
		return objectMapper;
	}

}
