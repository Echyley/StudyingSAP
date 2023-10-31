/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.dataimport.batch.stock.impl;

import de.hybris.platform.acceleratorservices.dataimport.batch.stock.StockImportAdapter;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.stock.StockService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * Abstract implementation of {@link StockImportAdapter}.
 */
public abstract class AbstractStockImportAdapter implements StockImportAdapter
{
	private static final Logger LOG = Logger.getLogger(AbstractStockImportAdapter.class);
	private static final String DEFAULT_WAREHOUSE_CODE = "default";
	private String warehouseCode = DEFAULT_WAREHOUSE_CODE;
	private ModelService modelService;
	private WarehouseService warehouseService;
	private StockService stockService;

	@Override
	public void performImport(final String cellValue, final Item product)
	{
		Assert.hasText(cellValue, "[Assertion failed] - cellValue argument must have text; it must not be null, empty, or blank");
		Assert.notNull(product, "[Assertion failed] - product is required; it must not be null\"");
		try
		{
			final String[] values = cellValue.split(":");
			final WarehouseModel warehouseModel;
			if (values.length > 1 && values[1] != null && !values[1].isEmpty())
			{
				warehouseModel = getWarehouseService().getWarehouseForCode(values[1]);
			}
			else
			{
				warehouseModel = getWarehouseService().getWarehouseForCode(getWarehouseCode());
			}
			final ProductModel productModel = getModelService().get(product);

			final int actualAmount = calculateActualAmount(values[0], warehouseModel, productModel);
			getStockService().updateActualStockLevel(productModel, warehouseModel, actualAmount, null);
		}
		catch (final RuntimeException e)
		{
			LOG.warn("Could not import stock for product " + product + ": " + e);
			throw e;
		}
		catch (final Exception e)
		{
			LOG.warn("Could not import stock for " + product + ": " + e);
			throw new SystemException("Could not import stock for " + product, e);
		}
	}

	protected abstract int calculateActualAmount(String inputField, WarehouseModel warehouseModel, ProductModel productModel);

	/**
	 * @param warehouseCode the warehouseCode to set
	 */
	public void setWarehouseCode(final String warehouseCode)
	{
		Assert.hasText(warehouseCode, "[Assertion failed] - warehouseCode argument must have text; it must not be null, empty, or blank");
		this.warehouseCode = warehouseCode;
	}

	/**
	 * @param modelService the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @param stockService the stockService to set
	 */
	@Required
	public void setStockService(final StockService stockService)
	{
		this.stockService = stockService;
	}

	/**
	 * @param warehouseService the warehouseService to set
	 */
	@Required
	public void setWarehouseService(final WarehouseService warehouseService)
	{
		this.warehouseService = warehouseService;
	}

	/**
	 * @return the warehouseCode
	 */
	protected String getWarehouseCode()
	{
		return warehouseCode;
	}

	/**
	 * @return the modelService
	 */
	protected ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @return the warehouseService
	 */
	protected WarehouseService getWarehouseService()
	{
		return warehouseService;
	}

	/**
	 * @return the stockService
	 */
	protected StockService getStockService()
	{
		return stockService;
	}
}
