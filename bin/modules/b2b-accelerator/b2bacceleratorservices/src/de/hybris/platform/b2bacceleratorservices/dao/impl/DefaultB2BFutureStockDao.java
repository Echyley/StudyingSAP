/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.dao.impl;

import de.hybris.platform.b2bacceleratorservices.dao.B2BFutureStockDao;
import de.hybris.platform.commerceservices.futurestock.dao.impl.DefaultFutureStockDao;


/**
 * Default implementation for {@link B2BFutureStockDao}.
 *
 * @deprecated Since 2205 Use {@link DefaultFutureStockDao} instead
 */
@Deprecated(since = "2205", forRemoval = true)
public class DefaultB2BFutureStockDao extends DefaultFutureStockDao implements B2BFutureStockDao
{
}
