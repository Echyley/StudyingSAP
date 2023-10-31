/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.futurestock;

/**
 * Service for 'Future Stock Management'.
 *
 * @spring.bean stockService
 * @deprecated Since 2205 Use {@link de.hybris.platform.commerceservices.futurestock.FutureStockService} instead
 */
@Deprecated(since = "2205", forRemoval = true)
public interface FutureStockService extends de.hybris.platform.commerceservices.futurestock.FutureStockService
{
}
