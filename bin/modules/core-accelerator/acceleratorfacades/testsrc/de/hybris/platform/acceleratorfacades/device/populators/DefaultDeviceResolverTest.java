/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorfacades.device.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorfacades.device.data.DeviceData;
import de.hybris.platform.acceleratorfacades.device.impl.DefaultDeviceResolver;

import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.BooleanUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultDeviceResolverTest
{

	@InjectMocks
	private final DefaultDeviceResolver deviceResolver = new DefaultDeviceResolver();

	@Mock
	private HttpServletRequest request;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void basedUserAgentDetectedToDeskTopSuccess()
	{
		BDDMockito.given(request.getHeader("User-Agent"))
				.willReturn("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0");
		final Enumeration<String> headerNames;
		final Vector<String> names = new Vector<>();
		names.add("Authorization");
		headerNames = names.elements();
		BDDMockito.given(request.getHeaderNames()).willReturn(headerNames);
		final DeviceData result = deviceResolver.resolveDevice(request);
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getMobileBrowser()));
		Assert.assertEquals(Boolean.TRUE, BooleanUtils.toBoolean(result.getDesktopBrowser()));
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getTabletBrowser()));
	}

	@Test
	public void basedUserAgentProfileExistDetectedToMobileSuccess()
	{
		BDDMockito.given(request.getHeader("x-wap-profile")).willReturn("http://nds1.nds.nokia.com/uaprof/N6600r100.xml");
		final DeviceData result = deviceResolver.resolveDevice(request);
		Assert.assertEquals(Boolean.TRUE, BooleanUtils.toBoolean(result.getMobileBrowser()));
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getDesktopBrowser()));
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getTabletBrowser()));
	}

	@Test
	public void basedUserAgentPrefixDetectedToMobileSuccess()
	{
		BDDMockito.given(request.getHeader("User-Agent")).willReturn("NOKIA5700/ UCWEB7.0.2.37/28/999");
		final DeviceData result = deviceResolver.resolveDevice(request);
		Assert.assertEquals(Boolean.TRUE, BooleanUtils.toBoolean(result.getMobileBrowser()));
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getDesktopBrowser()));
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getTabletBrowser()));
	}

	@Test
	public void basedAcceptHeaderDetectedToMobileSuccess()
	{
		BDDMockito.given(request.getHeader("Accept"))
				.willReturn("image/vnd.wap.wbmp,text/x-vCalendar,application/vnd.wap.wmlscriptc");
		final DeviceData result = deviceResolver.resolveDevice(request);
		Assert.assertEquals(Boolean.TRUE, BooleanUtils.toBoolean(result.getMobileBrowser()));
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getDesktopBrowser()));
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getTabletBrowser()));
	}

	@Test
	public void basedUserAgentContainAndroidNoMobileDetectedToTabletSuccess()
	{
		BDDMockito.given(request.getHeader("User-Agent")).willReturn(
				"Mozilla/5.0 (Linux; U; Android 3.0; en-us; Xoom Build/HRI39) AppleWebKit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13");
		final DeviceData result = deviceResolver.resolveDevice(request);
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getMobileBrowser()));
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getDesktopBrowser()));
		Assert.assertEquals(Boolean.TRUE, BooleanUtils.toBoolean(result.getTabletBrowser()));
	}

	@Test
	public void baseUserAgentContainSilkNoMobileDetectedToTabletSuccess()
	{
		BDDMockito.given(request.getHeader("User-Agent")).willReturn(
				"Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_3; en-us; Silk/1.0.22.79_10013310) AppleWebKit/533.16 (KHTML, like Gecko) Version/5.0 Safari/533.16 Silk-Accelerated=true");
		final DeviceData result = deviceResolver.resolveDevice(request);
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getMobileBrowser()));
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getDesktopBrowser()));
		Assert.assertEquals(Boolean.TRUE, BooleanUtils.toBoolean(result.getTabletBrowser()));
	}

	@Test
	public void baseUserAgentKeywordDetectedToTabletSuccess()
	{
		BDDMockito.given(request.getHeader("User-Agent")).willReturn(
				"Mozilla/5.0 (iPad; CPU OS 8_3 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko) Version/8.0 Mobile/12F5027d Safari/600.1.4");
		final DeviceData result = deviceResolver.resolveDevice(request);
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getMobileBrowser()));
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getDesktopBrowser()));
		Assert.assertEquals(Boolean.TRUE, BooleanUtils.toBoolean(result.getTabletBrowser()));
	}

	@Test
	public void baseUserAgentKeywordDetectedToMobileSuccess()
	{
		BDDMockito.given(request.getHeader("User-Agent")).willReturn(
				"Mozilla/5.0 (BlackBerry; U; BlackBerry 9800; en) AppleWebKit/534.1+ (KHTML, like Gecko) Version/6.0.0.337 Mobile Safari/534.1+");
		final DeviceData result = deviceResolver.resolveDevice(request);
		Assert.assertEquals(Boolean.TRUE, BooleanUtils.toBoolean(result.getMobileBrowser()));
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getDesktopBrowser()));
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getTabletBrowser()));
	}

	@Test
	public void basedHeaderNamesContainOperaMiniDetectedToMobileSuccess()
	{
		final Enumeration<String> headerNames;
		final Vector<String> names = new Vector<>();
		names.add("OperaMini");
		headerNames = names.elements();
		BDDMockito.given(request.getHeaderNames()).willReturn(headerNames);
		final DeviceData result = deviceResolver.resolveDevice(request);
		Assert.assertEquals(Boolean.TRUE, BooleanUtils.toBoolean(result.getMobileBrowser()));
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getDesktopBrowser()));
		Assert.assertEquals(Boolean.FALSE, BooleanUtils.toBoolean(result.getTabletBrowser()));
	}
}
