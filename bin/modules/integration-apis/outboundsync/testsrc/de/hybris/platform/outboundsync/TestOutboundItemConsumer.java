/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync;

import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundsync.activator.OutboundItemConsumer;
import de.hybris.platform.outboundsync.dto.OutboundItemDTO;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;

/**
 * A change consumer to be used in the tests.
 */
public class TestOutboundItemConsumer extends ExternalResource implements OutboundItemConsumer
{
	private static final Logger LOG = Log.getLogger(TestOutboundItemConsumer.class);

	private AtomicInteger invocations;

	@Override
	public void consume(final OutboundItemDTO outboundItemDTO)
	{
		LOG.info("Consuming {}", outboundItemDTO);
		invocations.incrementAndGet();
	}

	public int invocations()
	{
		return invocations.get();
	}

	public void reset()
	{
		LOG.debug("Resetting consume invocations");
		invocations = new AtomicInteger(0);
	}

	@Override
	protected void before()
	{
		reset();
	}

	@Override
	protected void after()
	{
		reset();
	}
}