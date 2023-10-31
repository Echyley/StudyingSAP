/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.velocity.core;



public interface CoreVelocityConfigurer<T, CTX>
{
	T configure();

	T configure(CTX context);
}
