/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service.impl;

import de.hybris.platform.outboundservices.event.EventType;
import de.hybris.platform.webhookservices.service.CloudEventConfigurationService;
import de.hybris.platform.webhookservices.service.CloudEventHeadersService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link CloudEventHeadersService}
 */
public class DefaultCloudEventHeadersService implements CloudEventHeadersService
{
	private static final String CLOUD_EVENT_TYPE = "%s.%s.%s.%s";
	private static final String CLOUD_EVENT_SOURCE = "/%s/%s%s";
	private static final String DATE_TIME_FORMATTER = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	private static final String SAP_COMMERCE_NAMESPACE = "sap.cx.commerce";
	private static final String CLOUD_EVENT_TYPE_HEADER_NAME = "ce-type";
	private static final String CLOUD_EVENT_SOURCE_HEADER_NAME = "ce-source";
	private static final String CLOUD_EVENT_SUBJECT_HEADER_NAME = "ce-subject";
	private static final String CLOUD_EVENT_SPECVERSION_HEADER_NAME = "ce-specversion";
	private static final String CLOUD_EVENT_TIME_HEADER_NAME = "ce-time";
	private static final String CLOUD_EVENT_ID_HEADER_NAME = "ce-id";
	private static final String CLOUD_EVENT_SAP_PASSPORT_HEADER_NAME = "ce-sappassport";

	private final CloudEventConfigurationService cloudEventConfigurationService;

	/**
	 * Constructor to create a DefaultCloudEventHeadersService
	 *
	 * @param cloudEventConfigurationService to get CloudEvent configuration value
	 */
	public DefaultCloudEventHeadersService(@NotNull final CloudEventConfigurationService cloudEventConfigurationService)
	{
		Preconditions.checkArgument(cloudEventConfigurationService != null, "CloudEventConfigurationService cannot be null");
		this.cloudEventConfigurationService = cloudEventConfigurationService;
	}

	@Override
	public HttpHeaders generateCloudEventHeaders(@NotNull final String integrationObjectCode, final String integrationKey,
	                                             @NotNull final EventType eventType, final String sapPassport)
	{
		Preconditions.checkArgument(integrationObjectCode != null, "integrationObjectCode cannot be null");
		Preconditions.checkArgument(eventType != null, "eventType cannot be null");

		final HttpHeaders cloudEventHeaders = new HttpHeaders();

		cloudEventHeaders.add(CLOUD_EVENT_ID_HEADER_NAME, getCloudEventId());
		cloudEventHeaders.add(CLOUD_EVENT_TYPE_HEADER_NAME, getCloudEventType(integrationObjectCode, eventType));
		cloudEventHeaders.add(CLOUD_EVENT_SOURCE_HEADER_NAME, getCloudEventSource());
		cloudEventHeaders.add(CLOUD_EVENT_SPECVERSION_HEADER_NAME, getCloudEventSpecVersion());
		cloudEventHeaders.add(CLOUD_EVENT_TIME_HEADER_NAME, getCloudEventDataTime());

		if(StringUtils.isNotBlank(integrationKey))
		{
			cloudEventHeaders.add(CLOUD_EVENT_SUBJECT_HEADER_NAME, integrationKey);
		}

		if (StringUtils.isNotBlank(sapPassport))
		{
			cloudEventHeaders.add(CLOUD_EVENT_SAP_PASSPORT_HEADER_NAME, sapPassport);
		}
		return cloudEventHeaders;
	}

	private String getCloudEventSource()
	{
		final String instanceId = cloudEventConfigurationService.getCloudEventSourceInstanceId();
		final String region = cloudEventConfigurationService.getCloudEventSourceRegion();
		return String.format(CLOUD_EVENT_SOURCE, region, SAP_COMMERCE_NAMESPACE,
				(StringUtils.isBlank(instanceId) ? "" : ("/" + instanceId)));
	}

	private String getCloudEventType(final String integrationObjectCode, final EventType eventType)
	{
		return String.format(CLOUD_EVENT_TYPE, SAP_COMMERCE_NAMESPACE,
				integrationObjectCode, eventType.getType(),
				cloudEventConfigurationService.getCloudEventTypeVersion());
	}

	private String getCloudEventSpecVersion()
	{
		return cloudEventConfigurationService.getCloudEventSpecVersion();
	}

	private String getCloudEventDataTime()
	{
		return LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern((DATE_TIME_FORMATTER)));
	}

	private String getCloudEventId()
	{
		return UUID.randomUUID().toString();
	}
}

