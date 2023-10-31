/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.service.impl;

import de.hybris.platform.apiregistryservices.exceptions.CredentialException;
import de.hybris.platform.apiregistryservices.services.ApiRegistryClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.hybris.charon.exp.ClientException;
import com.hybris.merchandising.exceptions.ReadDataException;
import com.hybris.merchandising.model.Strategy;
import com.hybris.merchandising.service.MerchStrategyServiceClient;
import com.hybris.merchandising.service.StrategyService;


/**
 * Default implementation of {@link StrategyService}.
 */
public class DefaultStrategyService implements StrategyService
{

	@Autowired
	@Qualifier("apiRegistryClientService")
	protected ApiRegistryClientService apiRegistryClientService;

	private static final Logger LOG = LoggerFactory.getLogger(DefaultStrategyService.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Strategy> getStrategies(final Integer pageNumber, final Integer pageSize)
	{
		final MerchStrategyServiceClient strategyClient = getClient();
		if (strategyClient != null)
		{

			if (pageSize != null && pageNumber != null)
			{
				return executeAndHandleException(() -> strategyClient.getStrategies(pageNumber, pageSize));
			}
			else
			{
				return executeAndHandleException(() -> strategyClient.getStrategies());
			}
		}
		return new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Strategy getStrategy(final String id)
	{
		final MerchStrategyServiceClient strategyClient = getClient();
		if (strategyClient != null)
		{
			return executeAndHandleException(() -> strategyClient.getStrategy(id));
		}
		return null;
	}

	/**
	 * Retrieves the configured {@link MerchStrategyServiceClient}.
	 *
	 * @return configured client.
	 */
	private MerchStrategyServiceClient getClient()
	{
		try
		{
			return apiRegistryClientService.lookupClient(MerchStrategyServiceClient.class);
		}
		catch (final CredentialException e)
		{
			LOG.error("Error retrieving client for Strategy Service : {} ", e.getMessage());
			LOG.debug("Exception", e);
			throw new ReadDataException(e.getMessage(), e);
		}
	}

	protected <R> R executeAndHandleException(final Supplier<R> supplier)
	{
		try
		{
			return supplier.get();
		}
		catch (final ClientException e)
		{
			LOG.debug("Failed to get data from CDS : ", e);
			throw e;
		}
		catch (final Exception e)
		{
			LOG.warn("Failed to get data from CDS. Error message : {} ", e.getMessage());
			LOG.debug("Exception", e);
			throw new ReadDataException(e.getMessage(), e);
		}
	}
}
