/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.clickstream.services;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@UnitTest
public class ClickStreamServiceTest
{
	static final String URL0 = "https://localhost:9002/yacceleratorstorefront/?site=electronics";
	static final String URL1 = "https://localhost:9002/yacceleratorstorefront/?site=electronics&"
			+ ClickStreamService.URL_SAP_OUTBOUND_ID + "abc";
	static final String URL2 = "https://localhost:9002/yacceleratorstorefront/?site=electronics&"
			+ ClickStreamService.URL_SAP_OUTBOUND_ID + "def&something=else";

	static final String URL5 = "https://localhost:9002/yacceleratorstorefront/?site=electronics&"
			+ ClickStreamService.URL_PK_CAMPAIGN + "abc";

	ClickStreamService clickStreamService;

	@Before
	public void setUp()
	{
		this.clickStreamService = new ClickStreamService();
	}

	@Test
	public void test_extractInitiativeId()
	{
		Assert.assertEquals(null, this.clickStreamService.extractInitiativeId(null, null));
		Assert.assertEquals("abc", this.clickStreamService.extractInitiativeId(URL5, null));
	}

	@Test
	public void test_extractSAPTrackingId()
	{
		Assert.assertEquals(null, this.clickStreamService.extractSAPTrackingId(null, null));
		Assert.assertEquals(null, this.clickStreamService.extractSAPTrackingId(URL0, null));
		Assert.assertEquals(null, this.clickStreamService.extractSAPTrackingId(null, URL0));
		Assert.assertEquals(null, this.clickStreamService.extractSAPTrackingId(URL0, URL0));

		Assert.assertEquals("abc", this.clickStreamService.extractSAPTrackingId(URL1, null));
		Assert.assertEquals("abc", this.clickStreamService.extractSAPTrackingId(null, URL1));
		Assert.assertEquals("abc", this.clickStreamService.extractSAPTrackingId(URL1, URL0));
		Assert.assertEquals("abc", this.clickStreamService.extractSAPTrackingId(URL0, URL1));
		Assert.assertEquals("abc", this.clickStreamService.extractSAPTrackingId(URL1, URL1));

		Assert.assertEquals("abc", this.clickStreamService.extractSAPTrackingId(URL1, URL2));
		Assert.assertEquals("def", this.clickStreamService.extractSAPTrackingId(URL2, URL1));
	}

}
