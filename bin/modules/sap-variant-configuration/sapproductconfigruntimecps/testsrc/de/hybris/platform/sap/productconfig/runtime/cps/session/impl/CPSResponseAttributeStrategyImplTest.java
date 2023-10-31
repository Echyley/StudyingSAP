/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.session.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.cache.CPSCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.NewCookie;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.charon.RawResponse;


/**
 * Unit test for {@link CPSResponseAttributeStrategyImpl}
 */
@UnitTest
public class CPSResponseAttributeStrategyImplTest
{
	private final CPSResponseAttributeStrategyImpl classUnderTest = new CPSResponseAttributeStrategyImpl();
	private final List<NewCookie> cookies = new ArrayList<>();

	private static final String CONFIG_ID = "cfgId";
	private static final String SESSION_COOKIE_NAME = "JSESSIONID";
	private static final String SESSION_COOKIE_VALUE = "928ABE7FA";
	private static final String CF_COOKIE_NAME = "__VCAP__";
	private static final String CF_COOKIE_VALUE = "98A5CDE6542";
	protected static final String VERSION = "eTag";
	@Mock
	private RawResponse rawResponse;
	protected final Optional<String> optinalETag = Optional.of(VERSION);

	@Mock
	private NewCookie sessionCookie;
	@Mock
	private NewCookie cfCookie;

	@Mock
	private CPSCache cpsCache;
	private List<String> cookieList = new ArrayList<>();


	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(sessionCookie.getName()).thenReturn(SESSION_COOKIE_NAME);
		Mockito.when(sessionCookie.getValue()).thenReturn(SESSION_COOKIE_VALUE);
		cookies.add(sessionCookie);
		Mockito.when(cfCookie.getName()).thenReturn(CF_COOKIE_NAME);
		Mockito.when(cfCookie.getValue()).thenReturn(CF_COOKIE_VALUE);
		Mockito.when(rawResponse.eTag()).thenReturn(optinalETag);

		cookies.add(cfCookie);
		when(rawResponse.getSetCookies()).thenReturn(cookies);
		cookieList = classUnderTest.convertToStringArray(cookies);
		Mockito.when(cpsCache.getCookies(CONFIG_ID)).thenReturn(cookieList);
		classUnderTest.setCpsCache(cpsCache);
	}


	public void testGetCookiesNotPresent()
	{
		assertNull(classUnderTest.getCookiesAsString("NOT_EXISTING"));
	}

	@Test
	public void testGetCookies()
	{
		final List<String> cookieList = classUnderTest.getCookiesAsString(CONFIG_ID);
		assertNotNull(cookieList);
		assertEquals(2, cookieList.size());
	}

	@Test
	public void testGetCookiesAsString()
	{
		final List<String> cookieListAsString = classUnderTest.getCookiesAsString(CONFIG_ID);
		assertNotNull(cookieListAsString);
		assertEquals(2, cookieListAsString.size());
	}

	@Test
	public void testGetCookiesAsStringOneCookies()
	{
		cookieList.remove(1);
		final List<String> cookieListAsString = classUnderTest.getCookiesAsString(CONFIG_ID);
		assertNotNull(cookieListAsString);
		assertEquals(1, cookieListAsString.size());
	}

	@Test
	public void testGetCookiesAsStringNull()
	{
		Mockito.when(cpsCache.getCookies(CONFIG_ID)).thenReturn(null);
		final List<String> cookieListAsString = classUnderTest.getCookiesAsString(CONFIG_ID);
		assertNull(cookieListAsString);
	}

	@Test
	public void testConvertToStringArray()
	{
		final List<String> cookiesAsString = classUnderTest.convertToStringArray(cookies);
		assertNotNull(cookiesAsString);
		assertEquals(2, cookiesAsString.size());
	}

	@Test
	public void testCookie2String()
	{
		assertEquals(SESSION_COOKIE_NAME + "=" + SESSION_COOKIE_VALUE, classUnderTest.cookie2String(sessionCookie));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetCookiesNull()
	{
		classUnderTest.setCookies(CONFIG_ID, null);
	}


	@Test
	public void testRemoveCookies()
	{
		classUnderTest.removeCookies(CONFIG_ID);
		Mockito.verify(cpsCache).removeCookies(CONFIG_ID);
	}

	@Test
	public void testCPSSessionCache()
	{
		assertEquals(cpsCache, classUnderTest.getCpsCache());
	}

	@Test
	public void testRetrieveETag()
	{
		assertEquals(VERSION, classUnderTest.retrieveETag(rawResponse, CONFIG_ID));
	}

	@Test(expected = IllegalStateException.class)
	public void testRetrieveETag_NoEtag()
	{
		Mockito.when(rawResponse.eTag()).thenReturn(Optional.empty());
		classUnderTest.retrieveETag(rawResponse, CONFIG_ID);
	}

	@Test
	public void testExtractAndSaveCookies()
	{
		classUnderTest.extractAndSaveCookies(rawResponse, CONFIG_ID);
		verify(cpsCache).setCookies(CONFIG_ID, cookieList);
	}

	@Test
	public void testExtractAndSaveCookiesNoCookies()
	{
		when(rawResponse.getSetCookies()).thenReturn(new ArrayList<>());
		classUnderTest.extractAndSaveCookies(rawResponse, CONFIG_ID);
		verify(cpsCache, times(0)).setCookies(CONFIG_ID, cookieList);
	}

	@Test
	public void testRetrieveETagAndSaveResponseAttributes()
	{
		assertEquals(VERSION, classUnderTest.retrieveETagAndSaveResponseAttributes(rawResponse, CONFIG_ID));
		verify(cpsCache).setCookies(CONFIG_ID, cookieList);
	}

	@Test
	public void testRetrieveETagAndSaveResponseAttributesNoCookies()
	{
		cookieList.clear();
		assertEquals(VERSION, classUnderTest.retrieveETagAndSaveResponseAttributes(rawResponse, CONFIG_ID));
		verify(cpsCache, times(0)).setCookies(CONFIG_ID, cookieList);
	}

	@Test
	public void testGetCachedCoockieValue()
	{
		assertEquals(CF_COOKIE_VALUE, classUnderTest.getCachedCoockieValue(CF_COOKIE_NAME, cookieList));
	}

	@Test
	public void testGetCachedCoockieValueEmptyCookieList()
	{
		assertNull(classUnderTest.getCachedCoockieValue(CF_COOKIE_NAME, new ArrayList<>()));
	}

	@Test
	public void testGetCachedCoockieValueCookieListNull()
	{
		assertNull(classUnderTest.getCachedCoockieValue(CF_COOKIE_NAME, null));
	}

	@Test
	public void testGetCachedCoockieWrongCookieName()
	{
		assertNull(classUnderTest.getCachedCoockieValue("WrongName", cookieList));
	}

	@Test
	public void testExtractAndSaveCookiesOneCookie()
	{
		cookies.remove(0);
		classUnderTest.extractAndSaveCookies(rawResponse, CONFIG_ID);
		verify(cpsCache, times(0)).removeCookies(Mockito.anyString());
	}

	@Test
	public void testExtractAndSaveCookiesOneCookiesAnotherValue()
	{
		cookies.remove(0);
		cookieList.remove(1);
		cookieList.add(CF_COOKIE_NAME + "=anotherValue");
		classUnderTest.extractAndSaveCookies(rawResponse, CONFIG_ID);
		verify(cpsCache).removeCookies(CONFIG_ID);
	}

}
