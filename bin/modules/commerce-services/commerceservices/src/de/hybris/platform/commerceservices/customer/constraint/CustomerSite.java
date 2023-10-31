/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.customer.constraint;

import de.hybris.platform.commerceservices.customer.validator.CustomerSiteValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


/**
 * The new class is used for validation for customer's new attribute "site" when isolated site employees create
 * new customer, the real validation mainly done by CustomerSiteValidator
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { CustomerSiteValidator.class })
@Documented
public @interface CustomerSite
{
	String message() default "de.hybris.platform.commerceservices.customer.constraint.CustomerSite.message";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
