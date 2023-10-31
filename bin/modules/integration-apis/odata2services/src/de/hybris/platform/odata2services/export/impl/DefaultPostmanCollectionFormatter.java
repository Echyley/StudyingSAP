/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl;

import de.hybris.platform.odata2services.export.PostmanCollectionFormatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The default implementation for the interface {@link PostmanCollectionFormatter}.
 */
public class DefaultPostmanCollectionFormatter implements PostmanCollectionFormatter
{

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String format(final PostmanCollection postmanCollection)
	{
		try
		{
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(postmanCollection);
		}
		catch (final JsonProcessingException ex)
		{
			throw new PostmanCollectionFormatterException(ex);
		}
	}

	void setObjectMapper(final ObjectMapper objectMapper)
	{
		this.objectMapper = objectMapper;
	}

}
