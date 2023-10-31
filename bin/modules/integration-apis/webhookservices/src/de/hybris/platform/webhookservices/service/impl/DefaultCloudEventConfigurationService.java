/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service.impl;

import de.hybris.platform.webhookservices.service.CloudEventConfigurationService;
import de.hybris.platform.util.Config;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

/**
 *  Default implementation of {@link CloudEventConfigurationService}
 */
public class DefaultCloudEventConfigurationService implements CloudEventConfigurationService
{
	private static final String SAP_CLOUD_EVENT_SPEC_VERSION_PROPERTY_KEY = "cloud.event.specVersion";
	private static final String SAP_CLOUD_EVENT_TYPE_VERSION_PROPERTY_KEY = "cloud.event.type.version";
	private static final String SAP_CLOUD_EVENT_REGION_PROPERTY_KEY = "cloud.event.region";
	private static final String CCV2_SERVICE_API_URL_PROPERTY_KEY = "ccv2.services.api.url.0";

	private static final String CCV2_SERVICE_API_URL_DEFAULT_VALUE = "https://localhost:9002";
	private static final String SAP_CLOUD_EVENT_SPEC_VERSION_DEFAULT_VALUE = "1.0";
	private static final String SAP_CLOUD_EVENT_TYPE_VERSION_DEFAULT_VALUE = "v1";
	private static final String SAP_CLOUD_EVENT_REGION_DEFAULT_VALUE = "ccv2Region";

	@Override
	public String getCloudEventSourceInstanceId()
	{
		final String url = getPropertyValue(CCV2_SERVICE_API_URL_PROPERTY_KEY, CCV2_SERVICE_API_URL_DEFAULT_VALUE);

		if (StringUtils.isBlank(url) || CCV2_SERVICE_API_URL_DEFAULT_VALUE.equals(url))
		{
			return getMachineName();
		}
		final String[] instanceIds = url.split("\\.");

		return (instanceIds.length > 1 && StringUtils.isNotBlank( instanceIds[1])) ? instanceIds[1] : "";
	}

	@Override
	public String getCloudEventSourceRegion()
	{
		return getPropertyValue(SAP_CLOUD_EVENT_REGION_PROPERTY_KEY, SAP_CLOUD_EVENT_REGION_DEFAULT_VALUE);
	}

	@Override
	public String getCloudEventTypeVersion()
	{
		return getPropertyValue(SAP_CLOUD_EVENT_TYPE_VERSION_PROPERTY_KEY, SAP_CLOUD_EVENT_TYPE_VERSION_DEFAULT_VALUE);
	}

	@Override
	public String getCloudEventSpecVersion()
	{
		return getPropertyValue(SAP_CLOUD_EVENT_SPEC_VERSION_PROPERTY_KEY, SAP_CLOUD_EVENT_SPEC_VERSION_DEFAULT_VALUE);
	}

	String getMachineName()
	{
		try
		{
			return InetAddress.getLocalHost().getHostName();
		}
		catch (final UnknownHostException e)
		{
			return "";
		}
	}

	String getPropertyValue(@NotNull final String propertyName, final String defaultValue)
	{
		return Config.getString(propertyName, defaultValue);
	}
}
