/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservicescommons.services;

import de.hybris.platform.commercewebservicescommons.dto.captcha.CaptchaValidationContext;

import javax.validation.constraints.NotNull;


/**
 * Service to choose available strategy to validate the given captcha response
 */
public interface CaptchaValidationService
{
	/**
	 * check whether need to validate the token and if yes, choose a strategy to validate the token
	 *
	 * @param captchaValidationContext a context which contains the token need to be validated.
	 * @throws de.hybris.platform.commercewebservicescommons.errors.exceptions.CaptchaValidationException   when captcha token validate failed
	 * @throws de.hybris.platform.commercewebservicescommons.errors.exceptions.CaptchaTokenMissingException when captcha token in context is blank
	 * @throws java.lang.IllegalArgumentException                                                           when captchaValidationContext is null
	 * @throws de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException                    when can't find proper validation strategy
	 */
	void validate(final @NotNull CaptchaValidationContext captchaValidationContext);
}
