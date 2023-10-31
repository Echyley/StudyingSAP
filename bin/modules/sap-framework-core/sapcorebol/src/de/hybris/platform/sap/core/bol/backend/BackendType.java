/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.bol.backend;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
{ ElementType.TYPE })
public @interface BackendType
{

	/**
	 * Type of backend.
	 */
	String value();

}
