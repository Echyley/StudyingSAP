/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.strategies.impl;

import de.hybris.platform.cpq.productconfig.services.BusinessContextService;
import de.hybris.platform.cpq.productconfig.services.client.CpqClient;
import de.hybris.platform.cpq.productconfig.services.client.CpqClientConstants;
import de.hybris.platform.cpq.productconfig.services.client.CpqClientUtil;
import de.hybris.platform.cpq.productconfig.services.client.DefaultCpqClientUtil;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationCloneData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationCloneRequest;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationCreateRequest;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationCreatedData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationCreatedResponseData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationPatchRequest;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.cpq.productconfig.services.strategies.CPQInteractionStrategy;
import de.hybris.platform.cpq.productconfig.services.strategies.ConfigurationLifecycleStrategy;
import rx.Observable;

import org.apache.log4j.Logger;

import com.hybris.charon.RawResponse;
import com.hybris.charon.exp.NotFoundException;


/**
 * Default implementation of {@link ConfigurationLifecycleStrategy}
 */
public class DefaultConfigurationLifecycleStrategy implements ConfigurationLifecycleStrategy
{
	private static final Logger LOG = Logger.getLogger(DefaultConfigurationLifecycleStrategy.class);

	private final CPQInteractionStrategy cpqInteractionStrategy;
	private final CpqClientUtil clientUtil;
	private final BusinessContextService businessContextService;

	/**
	 * Injection of mandatory beans
	 *
	 * @param cpqInteractionStrategy
	 * @param clientUtil
	 */
	public DefaultConfigurationLifecycleStrategy(final CPQInteractionStrategy cpqInteractionStrategy,
			final CpqClientUtil clientUtil, final BusinessContextService businessContextService)
	{
		this.cpqInteractionStrategy = cpqInteractionStrategy;
		this.clientUtil = clientUtil;
		this.businessContextService = businessContextService;
	}

	@Override
	public ConfigurationSummaryData getConfigurationSummary(final String configId)
	{
		String clientToken = getClientToken(businessContextService.getOwnerId());
		Observable<RawResponse<ConfigurationSummaryData>> configuration = getClient().getConfiguration(clientToken, configId);
		final RawResponse<ConfigurationSummaryData> rawResponse = clientUtil.toResponse(configuration);
		clientUtil.checkHTTPStatusCode("GET_SUMMARY", CpqClientConstants.HTTP_STATUS_OK, rawResponse);
		clientUtil.checkContentType("GET_SUMMARY", CpqClientConstants.HTTP_PRODUCE_APPL_JSON, rawResponse);
		return clientUtil.toResponse(rawResponse.content());
	}

	@Override
	public String createConfiguration(final String productCode, final String ownerId)
	{
		return initConfiguration(productCode, ownerId).getConfigId();
	}

	@Override
	public String cloneConfiguration(final String configId, final boolean isPermanent)
	{
		final ConfigurationCloneRequest cloneRequest = new ConfigurationCloneRequest();
		cloneRequest.setIsPermanent(isPermanent);
		final RawResponse<ConfigurationCloneData> rawResponse = clientUtil
				.toResponse(getClient().clone(getAdminToken(), true, configId, cloneRequest));
		clientUtil.checkHTTPStatusCode("CLONE", CpqClientConstants.HTTP_STATUS_CREATED, rawResponse);
		return clientUtil.toResponse(rawResponse.content()).getConfigurationId();
	}

	@Override
	public boolean deleteConfiguration(final String configId)
	{
		try
		{
			final RawResponse<?> rawResponse = clientUtil.toResponse(getClient().deleteConfiguration(getAdminToken(), configId));
			clientUtil.checkHTTPStatusCode("DELETE", CpqClientConstants.HTTP_STATUS_NO_CONTENT, rawResponse);
		}
		catch (final NotFoundException ex)
		{
			// do not fail, e.g.: if not was not found (could be already deleted)
			LOG.warn(String.format(DefaultCpqClientUtil.ERROR_MSG_WRONG_HTTP_STATUS_CODE, "DELETE",
					CpqClientConstants.HTTP_STATUS_NO_CONTENT, CpqClientConstants.HTTP_STATUS_NOT_FOUND), ex);
			return false;
		}
		return true;

	}

	@Override
	public void makeConfigurationPermanent(final String configId)
	{
		final ConfigurationPatchRequest requestData = new ConfigurationPatchRequest();
		requestData.setIsPermanent(true);
		final RawResponse<Object> rawResponse = clientUtil
				.toResponse(getClient().makePermanent(getAdminToken(), configId, requestData));
		clientUtil.checkHTTPStatusCode("MAKE_PERMANENT", CpqClientConstants.HTTP_STATUS_NO_CONTENT, rawResponse);
	}


	protected ConfigurationCreatedData initConfiguration(final String productCode, final String ownerId)
	{
		final ConfigurationCreateRequest createRequest = new ConfigurationCreateRequest();
		createRequest.setProductSystemId(productCode);
		final RawResponse<ConfigurationCreatedResponseData> response = clientUtil
				.toResponse(getClient().createConfiguration(getClientToken(ownerId), true, createRequest));
		clientUtil.checkHTTPStatusCode("CREATE", CpqClientConstants.HTTP_STATUS_CREATED, response);
		final String sessionId = response.header(CpqClientConstants.HTTP_HEADER_CPQ_SESSION_ID)
				.orElseThrow(() -> new IllegalArgumentException(String
				.format("CPQ did not sent a CPQ session id via request header '%s'", CpqClientConstants.HTTP_HEADER_CPQ_SESSION_ID)));
		final String configId = clientUtil.toResponse(response.content()).getConfigurationId();
		final ConfigurationCreatedData createdData = new ConfigurationCreatedData();
		createdData.setConfigId(configId);
		createdData.setSessionId(sessionId);
		return createdData;
	}

	protected CPQInteractionStrategy getCPQInteractionStrategy()
	{
		return cpqInteractionStrategy;
	}

	protected CpqClient getClient()
	{
		return getCPQInteractionStrategy().getClient();
	}

	protected String getAdminToken()
	{
		return getCPQInteractionStrategy().getAuthorizationString();
	}

	protected String getClientToken(final String ownerId)
	{
		return getCPQInteractionStrategy().getClientAuthorizationString(ownerId);
	}




}
