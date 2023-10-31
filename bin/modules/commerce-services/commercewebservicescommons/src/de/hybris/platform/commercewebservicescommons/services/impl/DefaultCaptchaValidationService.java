/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservicescommons.services.impl;

import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaProviderWsDtoType;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaValidationContext;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaValidationResult;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaVersionWsDtoType;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CaptchaTokenMissingException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CaptchaValidationException;
import de.hybris.platform.commercewebservicescommons.services.CaptchaValidationService;
import de.hybris.platform.commercewebservicescommons.strategies.CaptchaValidationStrategy;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Service to choose available strategy to validate the given captcha response
 */
public class DefaultCaptchaValidationService implements CaptchaValidationService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCaptchaValidationService.class);
	private static final CaptchaProviderWsDtoType DEFAULT_PROVIDER = CaptchaProviderWsDtoType.GOOGLE;
	private static final CaptchaVersionWsDtoType DEFAULT_VERSION = CaptchaVersionWsDtoType.V2;
	private static final String CONTEXT_IS_NULL_ERROR_MESSAGE = "Can not do captcha validation when context is not provided";
	private static final String NOT_FOUND_PROPER_STRATEGY_MESSAGE = "Can not found a proper strategy with provider %s and version %s";
	private static final String CAPTCHA_TOKEN_VALIDATE_FAILED = "validate captcha token failed for baseSiteId: {}, baseStoreId: {}, remoteIp: {}, failed reason :{}";
	private static final String CAPTCHA_TOKEN_FORMAT_INVALID = "format-invalid";
	public static final String CAPTCHA_TOKEN_MISSING = "captcha-token-missing";

	private List<CaptchaValidationStrategy> strategies;

	@Override
	public void validate(final CaptchaValidationContext context)
	{
		if (Objects.isNull(context))
		{
			LOGGER.warn(CONTEXT_IS_NULL_ERROR_MESSAGE);
			throw new IllegalArgumentException(CONTEXT_IS_NULL_ERROR_MESSAGE);
		}
		if (!context.isCaptchaCheckEnabled())
		{
			return;
		}
		if (StringUtils.isBlank(context.getCaptchaToken()))
		{
			this.logCaptchaValidationFailed(context, CAPTCHA_TOKEN_MISSING);
			throw new CaptchaTokenMissingException();
		}

		final CaptchaValidationStrategy validationStrategy = this.getProperStrategy(context);
		if (!validationStrategy.preCheckToken(context))
		{
			this.logCaptchaValidationFailed(context, CAPTCHA_TOKEN_FORMAT_INVALID);
			throw new CaptchaValidationException(CAPTCHA_TOKEN_FORMAT_INVALID);
		}

		final CaptchaValidationResult result = validationStrategy.validate(context);
		if (!result.isSuccess())
		{
			logCaptchaValidationFailed(context, result.getReason());
			throw new CaptchaValidationException(result.getReason());
		}
	}

	protected CaptchaValidationStrategy getProperStrategy(final CaptchaValidationContext context)
	{
		//for now , only support one strategy
		final CaptchaValidationStrategy captchaValidationStrategy = getStrategies().stream().filter(
						strategy -> DEFAULT_PROVIDER.equals(strategy.getProviderType()) && DEFAULT_VERSION.equals(strategy.getVersion()))
				.findFirst().orElse(null);
		if (captchaValidationStrategy == null)
		{
			final String failedReason = String.format(NOT_FOUND_PROPER_STRATEGY_MESSAGE, DEFAULT_PROVIDER, DEFAULT_VERSION);
			this.logCaptchaValidationFailed(context, failedReason);
			throw new NotFoundException(failedReason);
		}
		return captchaValidationStrategy;
	}

	protected List<CaptchaValidationStrategy> getStrategies()
	{
		return this.strategies;
	}

	public void setStrategies(final List<CaptchaValidationStrategy> strategies)
	{
		this.strategies = strategies;
	}

	private void logCaptchaValidationFailed(final CaptchaValidationContext context, final String failedReason)
	{
		LOGGER.warn(CAPTCHA_TOKEN_VALIDATE_FAILED, context.getBaseSiteId(), context.getBaseStoreId(), context.getRemoteIp(),
				failedReason);
	}
}
