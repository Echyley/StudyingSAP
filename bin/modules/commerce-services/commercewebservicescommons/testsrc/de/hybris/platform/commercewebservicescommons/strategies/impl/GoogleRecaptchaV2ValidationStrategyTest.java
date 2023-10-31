/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservicescommons.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.config.SiteConfigService;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaProviderWsDtoType;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaValidationContext;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaValidationResult;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaVersionWsDtoType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;


/**
 * Test suite for {@link de.hybris.platform.commercewebservicescommons.strategies.CaptchaValidationStrategy}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GoogleRecaptchaV2ValidationStrategyTest
{
	@InjectMocks
	private GoogleRecaptchaV2ValidationStrategy strategy;

	@Mock
	private CloseableHttpClient httpClient;

	@Mock
	private SiteConfigService siteConfigService;

	@Mock
	private CloseableHttpResponse httpResponse;

	@Mock
	private ProtocolVersion protocolVersion;

	private BasicHttpEntity httpEntity;

	private CaptchaValidationContext context;

	private final static String CAPTCHA_TOKEN = "03ANYolqv1YE-eCxznv6cWNaBf9VgzFn3mCGWXCQqoroWGk6fdjCj3OYt57wLZ5Wzo9frd6KQ4A2R6amEtYzCjOfDaqLg52M0eDx9eIQmSDRjfnW6nCQqFPv_MYVM5ZgA_08W7RoVFJ38r2kELfGw5p3-X7qs8AYKUH7F28DEtl_ytfeg-leklb7bfKgRfMhQtcuVRxIsp9DShbgWf_FzU809CVowLnbSKe3JhIBf0MQdhsO5Wgsl4_itmVFtnmtMd3ZfDBllc_3yYczsEMrmeewMsYHJ-IKPkK4YEiS0jXZ_Ubc3Cla4sQzrnbrF6GSRbh46G2pAEhmk8gqyUQXmkbdnR7iMLdZ5dZfZVjNQxQqusvzC7H7X66r3Lh3mCliZE2M1ChQejOMMaqGuCMnWX1CYE7JsBT0LZIhFezJsuoCQvQNrK396QdqhiymaQ4uFMlifv5GRa8aYF3XUfZsT6_yQgvsCm3C5edy7gc7KikhQ5HWOgQhq18VY";
	private final static String REMOTE_IP = "127.0.0.1";
	private final static String REFERER = "electronics.local";
	private final static String SITE_UID = "electronics";
	private final static String PRIVATE_KEY = "recaptcha.privatekey";
	private final static String HOST_NAME_CHECK_ENABLED = "recaptcha.hostname.check.enabled";

	@Before
	public void init() throws IOException
	{
		context = new CaptchaValidationContext();
		context.setRemoteIp(REMOTE_IP);
		context.setReferer(REFERER);
		context.setBaseSiteId(SITE_UID);
		httpEntity = new BasicHttpEntity();
		httpEntity.setContentType("application/json");
		Mockito.when(httpClient.execute(any())).thenReturn(httpResponse);
		Mockito.when(siteConfigService.getProperty(SITE_UID, PRIVATE_KEY)).thenReturn("privateKey");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateShouldValidateFailedSinceContextIsNull() throws IOException
	{
		//when
		strategy.validate(null);

		Mockito.verify(httpResponse, Mockito.times(0)).getStatusLine();
		Mockito.verify(httpClient, Mockito.times(0)).execute(any());
		Mockito.verify(httpResponse, Mockito.times(0)).getEntity();

		Mockito.verify(siteConfigService, Mockito.times(0)).getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED);
		Mockito.verify(siteConfigService, Mockito.times(0)).getProperty(context.getBaseSiteId(), PRIVATE_KEY);
	}

	@Test
	public void testValidateShouldValidateFailedSinceContextIsEmpty() throws IOException
	{
		//given
		Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, null));
		Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);

		String response = "{\"success\":false,\"error-codes\":[\"missing-input-response\"]}";
		InputStream inputStreamRoute = new ByteArrayInputStream(response.getBytes());
		httpEntity.setContent(inputStreamRoute);

		//when
		CaptchaValidationResult result = strategy.validate(context);

		//verify
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isSuccess());
		Assert.assertEquals("missing-input-response", result.getReason());

		Mockito.verify(httpResponse, Mockito.times(1)).getStatusLine();
		Mockito.verify(httpClient, Mockito.times(1)).execute(any());
		Mockito.verify(httpResponse, Mockito.times(1)).getEntity();

		Mockito.verify(siteConfigService, Mockito.times(0)).getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED);
		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), PRIVATE_KEY);
	}

	@Test
	public void testValidateShouldValidateFailedSincePrivateKeyIsEmpty() throws IOException
	{
		//given
		Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, null));
		Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
		context.setCaptchaToken(CAPTCHA_TOKEN);

		String response = "{\"success\":false,\"error-codes\":[\"missing-input-secret\"]}";
		InputStream inputStreamRoute = new ByteArrayInputStream(response.getBytes());
		httpEntity.setContent(inputStreamRoute);

		//when
		CaptchaValidationResult result = strategy.validate(context);

		//verify
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isSuccess());
		Assert.assertEquals("missing-input-secret", result.getReason());

		Mockito.verify(httpResponse, Mockito.times(1)).getStatusLine();
		Mockito.verify(httpClient, Mockito.times(1)).execute(any());
		Mockito.verify(httpResponse, Mockito.times(1)).getEntity();

		Mockito.verify(siteConfigService, Mockito.times(0)).getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED);
		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), PRIVATE_KEY);
	}

	@Test
	public void testValidateShouldValidateFailedSincePrivateKeyIsInvalid() throws IOException
	{
		//given
		Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, null));
		Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
		context.setCaptchaToken(CAPTCHA_TOKEN);

		String response = "{\"success\":false,\"error-codes\":[\"invalid-input-secret\"]}";
		InputStream inputStreamRoute = new ByteArrayInputStream(response.getBytes());
		httpEntity.setContent(inputStreamRoute);

		//when
		CaptchaValidationResult result = strategy.validate(context);

		//verify
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isSuccess());
		Assert.assertEquals("invalid-input-secret", result.getReason());

		Mockito.verify(httpResponse, Mockito.times(1)).getStatusLine();
		Mockito.verify(httpClient, Mockito.times(1)).execute(any());
		Mockito.verify(httpResponse, Mockito.times(1)).getEntity();

		Mockito.verify(siteConfigService, Mockito.times(0)).getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED);
		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), PRIVATE_KEY);
	}

	@Test
	public void testValidateShouldValidateFailedSinceTokenIsInvalid() throws IOException
	{
		//given
		Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, null));
		Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
		context.setCaptchaToken(CAPTCHA_TOKEN);

		String response = "{\"success\":false,\"error-codes\":[\"invalid-input-response\"]}";
		InputStream inputStreamRoute = new ByteArrayInputStream(response.getBytes());
		httpEntity.setContent(inputStreamRoute);

		//when
		CaptchaValidationResult result = strategy.validate(context);

		//verify
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isSuccess());
		Assert.assertEquals("invalid-input-response", result.getReason());

		Mockito.verify(httpResponse, Mockito.times(1)).getStatusLine();
		Mockito.verify(httpClient, Mockito.times(1)).execute(any());
		Mockito.verify(httpResponse, Mockito.times(1)).getEntity();

		Mockito.verify(siteConfigService, Mockito.times(0)).getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED);
		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), PRIVATE_KEY);
	}

	@Test
	public void testValidateShouldValidateFailedSinceTokenIsTimeoutOrDuplicated() throws IOException
	{
		//given
		Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, null));
		Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
		context.setCaptchaToken(CAPTCHA_TOKEN);

		String response = "{\"success\":false,\"error-codes\":[\"timeout-or-duplicate\"]}";
		InputStream inputStreamRoute = new ByteArrayInputStream(response.getBytes());
		httpEntity.setContent(inputStreamRoute);

		//when
		CaptchaValidationResult result = strategy.validate(context);

		//verify
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isSuccess());
		Assert.assertEquals("timeout-or-duplicate", result.getReason());

		Mockito.verify(httpResponse, Mockito.times(1)).getStatusLine();
		Mockito.verify(httpClient, Mockito.times(1)).execute(any());
		Mockito.verify(httpResponse, Mockito.times(1)).getEntity();

		Mockito.verify(siteConfigService, Mockito.times(0)).getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED);
		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), PRIVATE_KEY);
	}

	@Test
	public void testValidateShouldfailedSinceHostNameNotMatch() throws IOException
	{
		//given
		Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, null));
		Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
		context.setCaptchaToken(CAPTCHA_TOKEN);
		Mockito.when(siteConfigService.getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED)).thenReturn("true");
		String response = "{\"success\":true,\"error-codes\":[], \"hostname\": \"powertools.local\"}";
		InputStream inputStreamRoute = new ByteArrayInputStream(response.getBytes());
		httpEntity.setContent(inputStreamRoute);

		//when
		CaptchaValidationResult result = strategy.validate(context);

		//verify
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isSuccess());
		Assert.assertEquals("invalid-hostname", result.getReason());

		Mockito.verify(httpResponse, Mockito.times(1)).getStatusLine();
		Mockito.verify(httpClient, Mockito.times(1)).execute(any());
		Mockito.verify(httpResponse, Mockito.times(1)).getEntity();
		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED);
		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), PRIVATE_KEY);
	}

	@Test
	public void testValidateShouldSuccessWhenHostNameIsTestKey() throws IOException
	{
		//given
		Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, null));
		Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
		context.setCaptchaToken(CAPTCHA_TOKEN);
		Mockito.when(siteConfigService.getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED)).thenReturn("true");

		String response = "{\"success\":true,\"error-codes\":[], \"hostname\":\"testkey.google.com\"}";
		InputStream inputStreamRoute = new ByteArrayInputStream(response.getBytes());
		httpEntity.setContent(inputStreamRoute);

		//when
		CaptchaValidationResult result = strategy.validate(context);

		//verify
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isSuccess());
		Assert.assertNull(result.getReason());

		Mockito.verify(httpResponse, Mockito.times(1)).getStatusLine();
		Mockito.verify(httpClient, Mockito.times(1)).execute(any());
		Mockito.verify(httpResponse, Mockito.times(1)).getEntity();
		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED);
		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), PRIVATE_KEY);
	}

	@Test
	public void testValidateShouldSuccessWhenHostNameCheckIsEnabled() throws IOException
	{
		//given
		Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, null));
		Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
		context.setCaptchaToken(CAPTCHA_TOKEN);
		Mockito.when(siteConfigService.getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED)).thenReturn("true");

		String response = "{\"success\":true,\"error-codes\":[], \"hostname\":\"electronics.local\"}";
		InputStream inputStreamRoute = new ByteArrayInputStream(response.getBytes());
		httpEntity.setContent(inputStreamRoute);

		//when
		CaptchaValidationResult result = strategy.validate(context);

		//verify
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isSuccess());
		Assert.assertNull(result.getReason());

		Mockito.verify(httpResponse, Mockito.times(1)).getStatusLine();
		Mockito.verify(httpClient, Mockito.times(1)).execute(any());
		Mockito.verify(httpResponse, Mockito.times(1)).getEntity();

		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED);
		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), PRIVATE_KEY);
	}

	@Test
	public void testValidateShouldSuccessWhenHostNameCheckIsDisabled() throws IOException
	{
		//given
		Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, null));
		Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
		context.setCaptchaToken(CAPTCHA_TOKEN);
		Mockito.when(siteConfigService.getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED)).thenReturn("false");

		String response = "{\"success\":true,\"error-codes\":[], \"hostname\":\"powertools.local\"}";
		InputStream inputStreamRoute = new ByteArrayInputStream(response.getBytes());
		httpEntity.setContent(inputStreamRoute);

		//when
		CaptchaValidationResult result = strategy.validate(context);

		//verify
		Assert.assertNotNull(result);
		Assert.assertTrue(result.isSuccess());
		Assert.assertNull(result.getReason());

		Mockito.verify(httpResponse, Mockito.times(1)).getStatusLine();
		Mockito.verify(httpClient, Mockito.times(1)).execute(any());
		Mockito.verify(httpResponse, Mockito.times(1)).getEntity();

		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED);
		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), PRIVATE_KEY);
	}


	@Test
	public void testValidateShouldFailedSinceResponseIsEmpty() throws IOException
	{
		//given
		Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, null));
		Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
		context.setCaptchaToken(CAPTCHA_TOKEN);

		String response = "{\"success\":null,\"error-codes\":[]}";
		InputStream inputStreamRoute = new ByteArrayInputStream(response.getBytes());
		httpEntity.setContent(inputStreamRoute);

		//when
		CaptchaValidationResult result = strategy.validate(context);

		//verify
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isSuccess());
		Assert.assertEquals("The provider return response is invalid.", result.getReason());

		Mockito.verify(httpResponse, Mockito.times(1)).getStatusLine();
		Mockito.verify(httpClient, Mockito.times(1)).execute(any());
		Mockito.verify(httpResponse, Mockito.times(1)).getEntity();

		Mockito.verify(siteConfigService, Mockito.times(0)).getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED);
		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), PRIVATE_KEY);
	}

	@Test
	public void testValidateShouldFailedAndReasonIsNull() throws IOException
	{
		//given
		Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, null));
		Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
		context.setCaptchaToken(CAPTCHA_TOKEN);

		String response = "{\"success\":false,\"error-codes\":[]}";
		InputStream inputStreamRoute = new ByteArrayInputStream(response.getBytes());
		httpEntity.setContent(inputStreamRoute);

		//when
		CaptchaValidationResult result = strategy.validate(context);

		//verify
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isSuccess());
		Assert.assertNull(result.getReason());

		Mockito.verify(httpResponse, Mockito.times(1)).getStatusLine();
		Mockito.verify(httpClient, Mockito.times(1)).execute(any());
		Mockito.verify(httpResponse, Mockito.times(1)).getEntity();

		Mockito.verify(siteConfigService, Mockito.times(0)).getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED);
		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), PRIVATE_KEY);
	}

	@Test
	public void testValidateShouldFailedSinceSuccessInResponseIsNull() throws IOException
	{
		//given
		Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(protocolVersion, HttpStatus.SC_OK, null));
		Mockito.when(httpResponse.getEntity()).thenReturn(httpEntity);
		context.setCaptchaToken(CAPTCHA_TOKEN);

		String response = "{}";
		InputStream inputStreamRoute = new ByteArrayInputStream(response.getBytes());
		httpEntity.setContent(inputStreamRoute);

		//when
		CaptchaValidationResult result = strategy.validate(context);

		//verify
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isSuccess());
		Assert.assertEquals("The provider return response is invalid.", result.getReason());

		Mockito.verify(httpResponse, Mockito.times(1)).getStatusLine();
		Mockito.verify(httpClient, Mockito.times(1)).execute(any());
		Mockito.verify(httpResponse, Mockito.times(1)).getEntity();

		Mockito.verify(siteConfigService, Mockito.times(0)).getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED);
		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), PRIVATE_KEY);
	}

	@Test
	public void testValidateShouldValidateFailedReturn429() throws IOException
	{
		//given
		Mockito.when(httpResponse.getStatusLine()).thenReturn(new BasicStatusLine(protocolVersion, 429, "Too many requests"));
		context.setCaptchaToken(CAPTCHA_TOKEN);

		//when
		CaptchaValidationResult result = strategy.validate(context);

		//verify
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isSuccess());
		Assert.assertEquals("exceed-quota-limits", result.getReason());

		Mockito.verify(httpResponse, Mockito.times(1)).getStatusLine();
		Mockito.verify(httpClient, Mockito.times(1)).execute(any());
		Mockito.verify(httpResponse, Mockito.times(0)).getEntity();

		Mockito.verify(siteConfigService, Mockito.times(0)).getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED);
		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), PRIVATE_KEY);
	}

	@Test
	public void testValidateShouldValidateFailedReturn400() throws IOException
	{
		//given
		Mockito.when(httpResponse.getStatusLine())
				.thenReturn(new BasicStatusLine(protocolVersion, HttpStatus.SC_BAD_REQUEST, "Bad Request"));
		context.setCaptchaToken(CAPTCHA_TOKEN);

		//when
		CaptchaValidationResult result = strategy.validate(context);

		//verify
		Assert.assertNotNull(result);
		Assert.assertFalse(result.isSuccess());
		Assert.assertEquals("Bad Request", result.getReason());

		Mockito.verify(httpResponse, Mockito.times(1)).getStatusLine();
		Mockito.verify(httpClient, Mockito.times(1)).execute(any());
		Mockito.verify(httpResponse, Mockito.times(0)).getEntity();

		Mockito.verify(siteConfigService, Mockito.times(0)).getProperty(context.getBaseSiteId(), HOST_NAME_CHECK_ENABLED);
		Mockito.verify(siteConfigService, Mockito.times(1)).getProperty(context.getBaseSiteId(), PRIVATE_KEY);
	}

	@Test
	public void testGetProviderType()
	{
		CaptchaProviderWsDtoType providerType = strategy.getProviderType();
		Assert.assertEquals(CaptchaProviderWsDtoType.GOOGLE, providerType);
	}

	@Test
	public void testGetVersion()
	{
		CaptchaVersionWsDtoType version = strategy.getVersion();
		Assert.assertEquals(CaptchaVersionWsDtoType.V2, version);
	}

	@Test
	public void testPreCheckTokenShouldReturnFalseWhenPatternIsNotMatch()
	{
		CaptchaValidationContext context = new CaptchaValidationContext();
		context.setCaptchaToken("<html></html>");

		boolean checkResult = strategy.preCheckToken(context);

		Assert.assertFalse(checkResult);
	}

	@Test
	public void testPreCheckTokenShouldReturnTrueWhenPatternIsMatch()
	{
		CaptchaValidationContext context = new CaptchaValidationContext();
		context.setCaptchaToken("ABCDEFGHIJKLMNOPQRSTUVWXYZabcedefghijklmnopqrstuvwxyz1234567890_-");

		boolean checkResult = strategy.preCheckToken(context);

		Assert.assertTrue(checkResult);
	}
}
