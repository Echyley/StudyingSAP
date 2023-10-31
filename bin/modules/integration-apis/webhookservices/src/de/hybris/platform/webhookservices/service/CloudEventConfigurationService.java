/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookservices.service;

/**
 * A service that provides access to CloudEvent properties for CloudEvent headers.
 */
public interface CloudEventConfigurationService
{
	/**
	 * Get instance id
	 *
	 * @return instance id value
	 */
	String getCloudEventSourceInstanceId();

	/**
	 * Get CloudEvent source region
	 *
	 * @return region value
	 */
	String getCloudEventSourceRegion();

	/**
	 * Get CloudEvent type version value
	 *
	 * @return type version value
	 */
	String getCloudEventTypeVersion();

	/**
	 * Get CloudEvent specific version value
	 *
	 * @return specific version
	 */
	String getCloudEventSpecVersion();

}
