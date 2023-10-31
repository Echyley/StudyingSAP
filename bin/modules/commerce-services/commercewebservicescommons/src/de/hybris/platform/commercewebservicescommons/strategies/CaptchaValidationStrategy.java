/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservicescommons.strategies;

import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaProviderWsDtoType;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaValidationContext;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaValidationResult;
import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaVersionWsDtoType;


/**
 * Strategy to validate the captcha response provided
 */
public interface CaptchaValidationStrategy
{
	/**
	 * validate the given captcha-token and return the validate result.
	 *
	 * @param captchaValidationContext a context which contains the token need to be validated.
	 * @return CaptchaValidationResult : a dto has two fields "success" and "reason", the token is valid when success is true, otherwise , the token is invalid and reason is the error detail.
	 * @throws java.lang.IllegalArgumentException when captchaValidationContext is null
	 */
	CaptchaValidationResult validate(final CaptchaValidationContext captchaValidationContext);

	/**
	 * get the captcha provider
	 *
	 * @return CaptchaProviderWsDtoType: for now , just GOOGLE
	 */
	CaptchaProviderWsDtoType getProviderType();

	/**
	 * get the captcha version
	 *
	 * @return CaptchaVersionWsDtoType: for now , just GOOGLE's V2
	 */
	CaptchaVersionWsDtoType getVersion();

	/**
	 * pre-check the token before really call provider
	 *
	 * @param captchaValidationContext
	 * @return
	 */
	boolean preCheckToken(final CaptchaValidationContext captchaValidationContext);

}
