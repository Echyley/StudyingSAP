/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservicescommons.strategies.impl;

import de.hybris.platform.commerceservices.config.SiteConfigService;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaProviderWsDtoType;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaValidationContext;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaValidationResult;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaVersionWsDtoType;
import de.hybris.platform.commercewebservicescommons.dto.captcha.GoogleValidationResponse;
import de.hybris.platform.commercewebservicescommons.strategies.CaptchaValidationStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Strategy to validate the captcha response provided
 */
public class GoogleRecaptchaV2ValidationStrategy implements CaptchaValidationStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(GoogleRecaptchaV2ValidationStrategy.class);

	//parameters need by google
	private static final String PARAMETER_SECRET = "secret";
	private static final String PARAMETER_RESPONSE = "response";
	private static final String PARAMETER_REMOTE_IP = "remoteip";

	//recaptcha config
	private static final String RECAPTCHA_SECRET_KEY_PROPERTY = "recaptcha.privatekey";
	private static final String RECAPTCHA_HOST_NAME_CHECK_ENABLED_PROPERTY = "recaptcha.hostname.check.enabled";

	//verify url
	private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

	//hostname returned by mock server
	private static final String MOCK_HOST = "testkey.google.com";  // do not validate mock host

	//fields returned by google
	private static final String RESPONSE_FIELD_SUCCESS = "success";
	private static final String RESPONSE_FIELD_ERRORS = "error-codes";
	private static final String RESPONSE_FIELD_HOST_NAME = "hostname";

	private static final int TOO_MANY_REQUESTS = 429;
	//custom failed reason
	private static final String EXCEED_QUOTA_LIMITS = "exceed-quota-limits";
	private static final String INVALID_HOST_NAME = "invalid-hostname";
	private static final String RESPONSE_IS_INVALID = "The provider return response is invalid.";

	//regex for captcha token
	private static final String TOKEN_PATTERN = "^[0-9a-zA-Z-_]+$";
	private static final Pattern pattern = Pattern.compile(TOKEN_PATTERN);
	//log messages
	private static final String CONTEXT_IS_NULL_ERROR_MESSAGE = "Can not do captcha validation when context is not provided";
	private static final String BEGIN_TO_VERIFY_CAPTCHA_TOKEN = "validate captcha token begin for baseSiteId: {}, baseStoreId: {}, remoteIp: {}";
	private static final String HOST_NAME_NOT_MATCHED = "validate captcha token failed for baseSiteId: {}, baseStoreId:{}, remoteIP:{}, since referer in request :{}, hostName in response :{} is not matched";
	private HttpClient httpClient;
	private SiteConfigService siteConfigService;

	public GoogleRecaptchaV2ValidationStrategy(final HttpClient httpClient, final SiteConfigService siteConfigService)
	{
		this.httpClient = httpClient;
		this.siteConfigService = siteConfigService;
	}

	/**
	 * call the google validation endpoint to validate the captcha token
	 *
	 * @param context : a context contains the token needs to be validated
	 * @Return CaptchaValidationResult: success = true indicate the token is valid ; otherwise , the reason will indicate the error detail information.
	 */
	@Override
	public CaptchaValidationResult validate(final CaptchaValidationContext context)
	{
		if (Objects.isNull(context))
		{
			LOG.error(CONTEXT_IS_NULL_ERROR_MESSAGE);
			throw new IllegalArgumentException(CONTEXT_IS_NULL_ERROR_MESSAGE);
		}
		final String captchaToken = context.getCaptchaToken();
		final String remoteIp = context.getRemoteIp();

		//call google service to do the validation
		final HttpPost method = new HttpPost(RECAPTCHA_VERIFY_URL);

		final List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair(PARAMETER_SECRET,
				getSiteConfigService().getProperty(context.getBaseSiteId(), RECAPTCHA_SECRET_KEY_PROPERTY)));
		urlParameters.add(new BasicNameValuePair(PARAMETER_RESPONSE, captchaToken));
		urlParameters.add(new BasicNameValuePair(PARAMETER_REMOTE_IP, remoteIp));

		try
		{
			method.setEntity(new UrlEncodedFormEntity(urlParameters));
			LOG.info(BEGIN_TO_VERIFY_CAPTCHA_TOKEN, context.getBaseSiteId(), context.getBaseStoreId(), context.getRemoteIp());
			final HttpResponse httpResponse = getHttpClient().execute(method);
			return convertResponseToCaptchaValidationResult(context, httpResponse);
		}
		catch (IOException e)
		{
			final CaptchaValidationResult result = new CaptchaValidationResult();
			result.setSuccess(false);
			result.setReason(e.getMessage());
			return result;
		}
		finally
		{
			method.releaseConnection();
		}
	}

	@Override
	public CaptchaProviderWsDtoType getProviderType()
	{
		return CaptchaProviderWsDtoType.GOOGLE;
	}

	@Override
	public CaptchaVersionWsDtoType getVersion()
	{
		return CaptchaVersionWsDtoType.V2;
	}

	@Override
	public boolean preCheckToken(final CaptchaValidationContext captchaValidationContext)
	{
		final String token = captchaValidationContext.getCaptchaToken();
		if (Objects.nonNull(token))
		{
			final Matcher matcher = pattern.matcher(token);
			return matcher.matches();
		}
		return false;
	}

	protected HttpClient getHttpClient()
	{
		return httpClient;
	}

	protected SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	private CaptchaValidationResult convertResponseToCaptchaValidationResult(final CaptchaValidationContext context,
			final HttpResponse httpResponse) throws IOException
	{
		final StatusLine statusLine = httpResponse.getStatusLine();
		final int responseStatus = statusLine.getStatusCode();
		if (HttpStatus.SC_OK != responseStatus)
		{
			return processWhenGoogleReturnStatusIsNotOK(statusLine, responseStatus);
		}

		final GoogleValidationResponse validationResponse = convertHttpResponseToDto(httpResponse);
		final CaptchaValidationResult result = new CaptchaValidationResult();
		result.setSuccess(validationResponse.getSuccess());
		if (Boolean.TRUE.equals(validationResponse.getSuccess()))
		{
			validateHostName(context, validationResponse.getHostName(), result);
		}
		else
		{
			result.setReason(validationResponse.getErrorCodes().stream().findFirst().orElse(null));
		}
		return result;
	}

	private CaptchaValidationResult processWhenGoogleReturnStatusIsNotOK(final StatusLine statusLine, final int responseStatus)
	{
		final CaptchaValidationResult result = new CaptchaValidationResult();
		result.setSuccess(false);
		if (TOO_MANY_REQUESTS == responseStatus)
		{
			result.setReason(EXCEED_QUOTA_LIMITS);
		}
		else
		{
			result.setReason(statusLine.getReasonPhrase());
		}
		return result;
	}

	private GoogleValidationResponse convertHttpResponseToDto(HttpResponse httpResponse) throws IOException
	{
		GoogleValidationResponse response = new GoogleValidationResponse();
		if (httpResponse == null)
		{
			response.setSuccess(false);
			response.setErrorCodes(List.of(RESPONSE_IS_INVALID));
			return response;
		}

		final ObjectMapper objectMapper = new ObjectMapper();
		final Map<String, Object> validateResult = objectMapper.readValue(EntityUtils.toString(httpResponse.getEntity()),
				Map.class);
		if (MapUtils.isEmpty(validateResult) || validateResult.get(RESPONSE_FIELD_SUCCESS) == null)
		{
			response.setSuccess(false);
			response.setErrorCodes(List.of(RESPONSE_IS_INVALID));
		}
		else
		{
			boolean success = (Boolean) validateResult.get(RESPONSE_FIELD_SUCCESS);
			response.setSuccess(success);
			response.setHostName((String) validateResult.getOrDefault(RESPONSE_FIELD_HOST_NAME, null));
			response.setErrorCodes((List<String>) validateResult.getOrDefault(RESPONSE_FIELD_ERRORS, List.of()));
		}
		return response;
	}


	private boolean isHostNameCheckEnabled(final String baseSiteId)
	{
		final String hostNameCheckEnabledProperty = getSiteConfigService().getProperty(baseSiteId,
				RECAPTCHA_HOST_NAME_CHECK_ENABLED_PROPERTY);
		if (StringUtils.isBlank(hostNameCheckEnabledProperty))
		{
			return false;
		}
		return Boolean.valueOf(hostNameCheckEnabledProperty);
	}

	private void validateHostName(final CaptchaValidationContext context, final String hostName,
			final CaptchaValidationResult result)
	{
		if (isHostNameCheckEnabled(context.getBaseSiteId()))
		{
			//if the hostname is mock host, then just skip the check
			if (StringUtils.equals(hostName, MOCK_HOST))
			{
				return;
			}
			final String referer = context.getReferer();
			if (referer == null || !referer.contains(hostName))
			{
				LOG.error(HOST_NAME_NOT_MATCHED, context.getBaseSiteId(), context.getBaseStoreId(), context.getRemoteIp(), referer,
						hostName);
				result.setSuccess(false);
				result.setReason(INVALID_HOST_NAME);
			}
		}
	}
}
