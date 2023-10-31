/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.search.solrfacetsearch.provider.impl;

import de.hybris.platform.commerceservices.stock.CommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractValueResolver;
import de.hybris.platform.store.services.BaseStoreService;

import com.sap.retail.oaa.commerce.services.common.util.CommonUtils;


/**
 *
 */
public class RoughStockIndicatorValueResolver extends AbstractValueResolver<ProductModel, String, Object>
{

	private CommerceStockService stockService;
	private BaseStoreService baseStoreService = null;
	private CommonUtils commonUtils;

	@Override
	protected void addFieldValues(final InputDocument document, final IndexerBatchContext batchContext,
			final IndexedProperty indexedProperty, final ProductModel productModel,
			final ValueResolverContext<String, Object> resolverContext) throws FieldValueProviderException
	{
		if (getCommonUtils().isCOSEnabled())
		{
		document.addField(indexedProperty,
				stockService.getStockLevelStatusForProductAndBaseStore(productModel, baseStoreService.getCurrentBaseStore()),
				resolverContext.getFieldQualifier());
		}
	}

	/**
	 * @return the baseStoreService
	 */
	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the stockService
	 */
	public CommerceStockService getStockService()
	{
		return stockService;
	}

	/**
	 * @param stockService
	 *           the stockService to set
	 */
	public void setStockService(final CommerceStockService stockService)
	{
		this.stockService = stockService;
	}

	public CommonUtils getCommonUtils()
	{
		return commonUtils;
	}

	public void setCommonUtils(final CommonUtils commonUtils)
	{
		this.commonUtils = commonUtils;
	}


}
