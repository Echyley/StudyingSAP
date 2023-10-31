/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.futurestock.dao.impl;

import de.hybris.platform.commerceservices.futurestock.dao.FutureStockDao;
import de.hybris.platform.commerceservices.model.FutureStockModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Default implementation for {@link FutureStockDao}.
 */
public class DefaultFutureStockDao extends DefaultGenericDao<FutureStockModel> implements FutureStockDao
{
	public DefaultFutureStockDao()
	{
		super(FutureStockModel._TYPECODE);
	}

	@Override
	public List<FutureStockModel> getFutureStocksByProductCode(final String productCode)
	{
		final Map<String, String> params = new HashMap<>();
		params.put(FutureStockModel.PRODUCTCODE, productCode);
		return find(params);
	}
}
