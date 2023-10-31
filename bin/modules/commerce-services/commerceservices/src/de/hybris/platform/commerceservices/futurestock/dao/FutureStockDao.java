/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.futurestock.dao;

import de.hybris.platform.commerceservices.model.FutureStockModel;

import java.util.List;


/**
 * DAO for Future Stock.
 */
public interface FutureStockDao
{
	List<FutureStockModel> getFutureStocksByProductCode(final String productCode);
}
