/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.dataimport.batch.stock.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;

/**
 * Default implementation of {@link AbstractStockImportAdapter}.
 */
public class DefaultStockImportAdapter extends AbstractStockImportAdapter
{
	@Override
	protected int calculateActualAmount(final String inputField, final WarehouseModel warehouseModel, final ProductModel productModel)
	{
		return Integer.parseInt(inputField);
	}

}
