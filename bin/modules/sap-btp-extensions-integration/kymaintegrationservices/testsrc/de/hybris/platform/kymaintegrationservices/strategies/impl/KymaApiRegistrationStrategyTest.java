/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.kymaintegrationservices.strategies.impl;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.jalo.ConsistencyCheckException;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class KymaApiRegistrationStrategyTest
{
	private KymaApiRegistrationStrategy kymaApiRegistrationStrategy;
	private static String SYSTEM_ID = "9dce1bb8-7d88-4c82-ad32-011559f5283b";

	@Before
	public void setUp() throws InterruptedException, ConsistencyCheckException
	{
		kymaApiRegistrationStrategy = new KymaApiRegistrationStrategy();
		kymaApiRegistrationStrategy.setJacksonObjectMapper(new ObjectMapper());
	}

	@Test
	public void testExtractServiceIdFromResponseBodyValid()
	{
		final String jsonResponseBody = generateJsonForSystemId(SYSTEM_ID);

		final String actSystemId = kymaApiRegistrationStrategy.extractServiceIdFromResponseBody(jsonResponseBody);
		assertEquals(SYSTEM_ID, actSystemId);
	}

	@Test
	public void testExtractServiceIdFromResponseBodyInvalid()
	{
		final String invalidJsonResponseBody = SYSTEM_ID;

		final String actSystemId = kymaApiRegistrationStrategy.extractServiceIdFromResponseBody(invalidJsonResponseBody);
		assertEquals(StringUtils.EMPTY, actSystemId);
	}

	@Test
	public void testExtractServiceIdFromResponseBodyEmpty()
	{
		final String actSystemId = kymaApiRegistrationStrategy.extractServiceIdFromResponseBody(null);
		assertEquals(StringUtils.EMPTY, actSystemId);
	}

	private String generateJsonForSystemId(final String id)
	{
		return "{\"id\": \"" + id + "\"}";
	}

}
