/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.futurestock.impl;

import de.hybris.platform.acceleratorservices.futurestock.FutureStockService;
import de.hybris.platform.commerceservices.futurestock.impl.DefaultFutureStockService;


/**
 * Default B2B implementation for {@link FutureStockService}. Gets future availabilities for a product.
 *
 * @deprecated Since 2205 Use {@link DefaultFutureStockService} instead
 */
@Deprecated(since = "2205", forRemoval = true)
public class DefaultB2BFutureStockService extends DefaultFutureStockService implements FutureStockService
{
}
