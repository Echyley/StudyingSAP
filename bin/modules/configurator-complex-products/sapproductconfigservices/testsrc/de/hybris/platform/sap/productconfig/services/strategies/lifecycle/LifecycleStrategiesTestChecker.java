/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle;


public interface LifecycleStrategiesTestChecker
{

	void checkLinkToProduct(final String productCode, final String configId);

	void checkBasicData(final String userName, final String configId);

	void checkLinkToCart(final String configId, final String cartItemHandle, final boolean isDraft);

	void checkConfigDeleted(final String configId, final String cartItemKey, boolean checkCache);

	void checkNumberOfConfigsPersisted(final int numExpected);

	default void checkNumberOfConfigsPersisted(final String message, final int numExpected)
	{
		checkNumberOfConfigsPersisted(numExpected);
	}

	default void checkProductConfiguration(final String configId, final String userName)
	{
		checkProductConfiguration(configId, userName, null, null, false);
	}

	default void checkProductConfiguration(final String configId, final String userName, final String productCode)
	{
		checkProductConfiguration(configId, userName, productCode, null, false);
	}

	default void checkProductConfiguration(final String configId, final String userName, final String productCode,
			final String cartItemHandle, final boolean isDraft)
	{
		checkBasicData(userName, configId);
		checkLinkToProduct(productCode, configId);
		checkLinkToCart(configId, cartItemHandle, isDraft);
	}

	default void checkConfigDeleted(final String configId, final String cartItemKey)
	{
		checkConfigDeleted(configId, cartItemKey, true);
	}

}
