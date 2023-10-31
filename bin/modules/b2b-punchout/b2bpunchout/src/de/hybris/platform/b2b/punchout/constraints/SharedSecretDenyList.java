/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.constraints;

import de.hybris.platform.b2b.punchout.model.PunchOutCredentialModel;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks field values for validation against a deny list.
 * {@link PunchOutCredentialModel#getSharedsecret()}
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = SharedSecretDenyListValidator.class)
@Documented
public @interface SharedSecretDenyList
{
	String message() default "{de.hybris.platform.b2b.punchout.constraints.SharedSecretDenyList.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
