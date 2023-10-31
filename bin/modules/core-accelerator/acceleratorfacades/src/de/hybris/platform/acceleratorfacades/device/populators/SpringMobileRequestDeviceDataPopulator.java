/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorfacades.device.populators;

import de.hybris.platform.acceleratorfacades.device.DeviceResolver;
import de.hybris.platform.acceleratorfacades.device.data.DeviceData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;


public class SpringMobileRequestDeviceDataPopulator implements Populator<HttpServletRequest, DeviceData>
{

	private DeviceResolver deviceResolver;

	@Override
	public void populate(final HttpServletRequest source, final DeviceData target) throws ConversionException
	{
		final DeviceData device = deviceResolver.resolveDevice(source);

		target.setUserAgent(source.getHeader("User-Agent"));
		target.setDesktopBrowser(BooleanUtils.toBoolean(device.getDesktopBrowser()));
		target.setTabletBrowser(BooleanUtils.toBoolean(device.getTabletBrowser()));
		target.setMobileBrowser(BooleanUtils.toBoolean(device.getMobileBrowser()));
	}

	public String toStringDeviceData(final DeviceData device)
	{
		final StringBuilder builder = new StringBuilder(73);
		builder.append("[DeviceData ");
		builder.append("id").append('=').append(device.getId()).append(", ");
		builder.append("userAgent").append('=').append(device.getUserAgent()).append(", ");
		builder.append("capabilities").append('=').append(device.getCapabilities()).append(", ");
		builder.append("desktop").append('=').append(device.getDesktopBrowser()).append(", ");
		builder.append("mobile").append('=').append(device.getMobileBrowser()).append(", ");
		builder.append("tablet").append('=').append(device.getTabletBrowser()).append(", ");
		builder.append(']');
		return builder.toString();
	}

	public DeviceResolver getDeviceResolver()
	{
		return deviceResolver;
	}

	@Required
	public void setDeviceResolver(final DeviceResolver deviceResolver)
	{
		this.deviceResolver = deviceResolver;
	}
}
