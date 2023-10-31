/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercewebservicescommons.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation for occ api to check the captcha response
 * <p>
 * CaptchaAware is used to intercept these requests which want to do the validation of captcha response, like registration.
 * {@code CaptchaValidationException} will be thrown if the given captcha token is invalid.
 * or {@code CaptchaTokenMissingException} will be thrown if the captchaCheckEnabled=true but the header "sap-commerce-cloud-captcha-token" is not provided.
 * <p>
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CaptchaAware
{
}
