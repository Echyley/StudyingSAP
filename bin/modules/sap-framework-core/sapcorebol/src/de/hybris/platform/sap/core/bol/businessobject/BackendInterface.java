/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.bol.businessobject;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Defines the type of the backend interface which belongs to the business object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
{ ElementType.TYPE })
public @interface BackendInterface
{

	/**
	 * Interface the specific {@link BackendBusinessObject} implements.
	 */
	Class<? extends BackendBusinessObject> value();

}
