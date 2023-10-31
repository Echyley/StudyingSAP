/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.dataimport.batch.stock.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;

/**
 * Incremental implementation of {@link AbstractStockImportAdapter}.
 */
public class IncrementalStockImportAdapter extends DefaultStockImportAdapter
{
	@Override
	protected int calculateActualAmount(final String inputField, final WarehouseModel warehouseModel, final ProductModel productModel)
	{
		int actualAmount = Integer.parseInt(inputField);
		actualAmount += getStockService().getStockLevelAmount(productModel, warehouseModel);

		if (actualAmount < 0)
		{
			actualAmount = 0;
		}
		return actualAmount;
	}
}
