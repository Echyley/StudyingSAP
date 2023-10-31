/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorfacades.device.impl;

import de.hybris.platform.acceleratorfacades.device.DeviceResolver;
import de.hybris.platform.acceleratorfacades.device.data.DeviceData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


public class DefaultDeviceResolver implements DeviceResolver
{

	private static final String MOBILE = "mobile";
	private static final String TABLET = "tablet";
	private static final String DESKTOP = "desktop";
	private static final int BEGIN_INDEX = 0;
	private static final int END_INDEX = 4;
	private final List<String> mobileUserAgentPrefixes = new ArrayList<>();
	private final List<String> mobileUserAgentKeywords = new ArrayList<>();
	private final List<String> tabletUserAgentKeywords = new ArrayList<>();
	private final List<String> desktopUserAgentKeywords = new ArrayList<>();


	public DefaultDeviceResolver()
	{
		this.init();
	}

	public DefaultDeviceResolver(final List<String> desktopUserAgentKeywords)
	{
		this.init();
		this.desktopUserAgentKeywords.addAll(desktopUserAgentKeywords);
	}

	@Override
	public DeviceData resolveDevice(final HttpServletRequest request)
	{
		String userAgent = request.getHeader("User-Agent");
		// User Agent keyword detection of desktop devices
		if (userAgent != null)
		{
			userAgent = userAgent.toLowerCase();
			for (final String keyword : desktopUserAgentKeywords)
			{
				if (userAgent.contains(keyword))
				{
					return getDeviceData(DESKTOP);
				}
			}
		}
		// User Agent Prof detection
		if (request.getHeader("x-wap-profile") != null || request.getHeader("Profile") != null)
		{
			return getDeviceData(MOBILE);
		}
		// User Agent prefix detection
		if (userAgent != null && userAgent.length() >= END_INDEX)
		{
			final String prefix = userAgent.substring(BEGIN_INDEX, END_INDEX).toLowerCase();
			if (mobileUserAgentPrefixes.contains(prefix))
			{
				return getDeviceData(MOBILE);
			}
		}
		// Accept-header based detection
		final String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("wap"))
		{
			return getDeviceData(MOBILE);
		}
		// User Agent keyword detection for Mobile and Tablet devices
		if (userAgent != null)
		{
			userAgent = userAgent.toLowerCase();
			// Android special case
			if (userAgent.contains("android") && !userAgent.contains("mobile"))
			{
				return getDeviceData(TABLET);
			}
			// Kindle Fire special case
			if (userAgent.contains("silk") && !userAgent.contains("mobile"))
			{
				return getDeviceData(TABLET);
			}
			for (final String keyword : tabletUserAgentKeywords)
			{
				if (userAgent.contains(keyword))
				{
					return getDeviceData(TABLET);
				}
			}
			for (final String keyword : mobileUserAgentKeywords)
			{
				if (userAgent.contains(keyword))
				{
					return getDeviceData(MOBILE);
				}
			}
		}
		// OperaMini special case
		final Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements())
		{
			final String header = headers.nextElement();
			if (header.contains("OperaMini"))
			{
				return getDeviceData(MOBILE);
			}
		}
		return getDeviceData(DESKTOP);
	}

	protected DeviceData getDeviceData(final String deviceType)
	{
		final DeviceData deviceData = new DeviceData();
		switch (deviceType)
		{
			case MOBILE:
				deviceData.setMobileBrowser(true);
				break;
			case TABLET:
				deviceData.setTabletBrowser(true);
				break;
			case DESKTOP:
				deviceData.setDesktopBrowser(true);
				break;
		}
		return deviceData;
	}

	//    List of user agent prefixes that identify mobile devices.
	protected List<String> getMobileUserAgentPrefixes()
	{
		return this.mobileUserAgentPrefixes;
	}

	//    List of user agent keywords that identify mobile devices.
	protected List<String> getMobileUserAgentKeywords()
	{
		return this.mobileUserAgentKeywords;
	}

	//    List of user agent keywords that identify tablet devices.
	protected List<String> getTabletUserAgentKeywords()
	{
		return this.tabletUserAgentKeywords;
	}

	//    List of user agent keywords that identify desktop devices.
	protected List<String> getDesktopUserAgentKeywords()
	{
		return this.desktopUserAgentKeywords;
	}

	protected final void init()
	{
		this.getMobileUserAgentPrefixes().addAll(Arrays.asList(KNOWN_MOBILE_USER_AGENT_PREFIXES));
		this.getMobileUserAgentKeywords().addAll(Arrays.asList(KNOWN_MOBILE_USER_AGENT_KEYWORDS));
		this.getTabletUserAgentKeywords().addAll(Arrays.asList(KNOWN_TABLET_USER_AGENT_KEYWORDS));
	}

	private static final String[] KNOWN_MOBILE_USER_AGENT_PREFIXES = new String[]
	{ "w3c ", "w3c-", "acs-", "alav", "alca", "amoi", "audi", "avan", "benq", "bird", "blac", "blaz", "brew", "cell", "cldc",
			"cmd-", "dang", "doco", "eric", "hipt", "htc_", "inno", "ipaq", "ipod", "jigs", "kddi", "keji", "leno", "lg-c", "lg-d",
			"lg-g", "lge-", "lg/u", "maui", "maxo", "midp", "mits", "mmef", "mobi", "mot-", "moto", "mwbp", "nec-", "newt", "noki",
			"palm", "pana", "pant", "phil", "play", "port", "prox", "qwap", "sage", "sams", "sany", "sch-", "sec-", "send", "seri",
			"sgh-", "shar", "sie-", "siem", "smal", "smar", "sony", "sph-", "symb", "t-mo", "teli", "tim-", "tosh", "tsm-", "upg1",
			"upsi", "vk-v", "voda", "wap-", "wapa", "wapi", "wapp", "wapr", "webc", "winw", "winw", "xda ", "xda-" };
	private static final String[] KNOWN_MOBILE_USER_AGENT_KEYWORDS = new String[]
	{ "blackberry", "webos", "ipod", "lge vx", "midp", "maemo", "mmp", "mobile", "netfront", "hiptop", "nintendo DS", "novarra",
			"openweb", "opera mobi", "opera mini", "palm", "psp", "phone", "smartphone", "symbian", "up.browser", "up.link", "wap",
			"windows ce" };
	private static final String[] KNOWN_TABLET_USER_AGENT_KEYWORDS = new String[]
	{ "ipad", "playbook", "hp-tablet", "kindle" };
}
