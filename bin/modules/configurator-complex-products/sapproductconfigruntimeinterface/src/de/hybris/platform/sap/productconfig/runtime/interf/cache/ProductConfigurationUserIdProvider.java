/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.interf.cache;

/**
 * Provides a user id for the current user
 */
public interface ProductConfigurationUserIdProvider
{
	/**
	 * Obtains an id that uniquely identifies the current user even if the session is anonymous
	 *
	 * @return user id
	 */
	String getCurrentUserId();

	/**
	 * @return User is anonymous?
	 */
	boolean isAnonymousUser();
}
