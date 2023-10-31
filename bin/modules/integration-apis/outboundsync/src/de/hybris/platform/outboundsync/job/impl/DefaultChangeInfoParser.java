/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl;

import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundsync.dto.ChangeInfo;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of the {@link ChangeInfoParser} that parses info from a JSON string of the following format:
 * {@code "{ "key": "..", "type": "..", "rootType": ".." }"}, in which order of the attributes is arbitrary and attribute "key"
 * contains value of the changed item integration key; attribute "type" contains value of the item type; attribute "rootType"
 * contains value of the item type that is navigated from the changed item and corresponds to the root item in the
 * integration object used to synchronize the change.
 */
public class DefaultChangeInfoParser implements ChangeInfoParser
{
	private static final Logger LOG = Log.getLogger(DefaultChangeInfoParser.class);

	private static final Set<String> RESERVED_INFO_VALUES = Collections.singleton("created");

	private boolean failOnUnknownProperty;

	/**
	 * {@inheritDoc}
	 *
	 * @return an optional containing the structured presentation of the parsed info; or {@code Optional.empty()}, if the info
	 * could not be parsed, or {@code info} is {@code null} or blank.
	 */
	@Override
	public Optional<ChangeInfo> parse(final String info)
	{
		if (StringUtils.isNotBlank(info) && isNotReservedValue(info))
		{
			try
			{
				final ChangeInfo changeInfo = objectMapper().readValue(info, ChangeInfo.class);
				return Optional.of(changeInfo);
			}
			catch (final JsonProcessingException e)
			{
				LOG.warn("Failed to parse info '{}'", info, e);
			}
		}
		return Optional.empty();
	}

	private boolean isNotReservedValue(final String info)
	{
		return !RESERVED_INFO_VALUES.contains(info);
	}

	/**
	 * Returns object mapper to be used for parsing
	 *
	 * @return an object mapper instance
	 */
	protected ObjectMapper objectMapper()
	{
		return new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, failOnUnknownProperty);
	}

	/**
	 * Specifies whether to fail parsing and to return {@code Optional.empty()} from the {@link #parse(String)} method, if the
	 * info string contains a property that is not "key", "type" or "rootType"
	 *
	 * @param value {@code true}, if the parsing should fail; {@code false} otherwise.
	 */
	public void setFailOnUnknownProperty(final boolean value)
	{
		failOnUnknownProperty = value;
	}
}
