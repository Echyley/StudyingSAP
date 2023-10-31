/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.validation.constraints;

import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;


/**
 * Identifier constraint.
 */
@Documented
@Target(
{ FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SnIdentifierValidator.class)
public @interface SnIdentifier
{

	String message() default "{de.hybris.platform.searchservices.validation.constraints.SnIdentifier.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
