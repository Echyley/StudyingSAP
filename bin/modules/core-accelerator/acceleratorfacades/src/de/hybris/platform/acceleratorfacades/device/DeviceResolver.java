/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorfacades.device;

import de.hybris.platform.acceleratorfacades.device.data.DeviceData;

import javax.servlet.http.HttpServletRequest;


/**
 * device detection
 */
public interface DeviceResolver
{
	/**
	 * Detects whether the accessed device belongs to a mobile, tablet or desktop.
	 * 
	 * @param request
	 *           the request
	 * @return the detected device data
	 */
	DeviceData resolveDevice(HttpServletRequest request);
}
