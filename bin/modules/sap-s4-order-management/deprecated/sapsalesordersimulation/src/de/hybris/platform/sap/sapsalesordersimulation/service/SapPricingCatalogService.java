/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service;

import java.util.List;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.PriceService;

/**
 *	Sap Pricing Catalog service.
 */
public interface SapPricingCatalogService extends PriceService
{
	/**
	 * Method to get price information for products 
	 * 
	 * @param models List<ProductModel>
	 * @return List<PriceInformation>
	 */
	public List<PriceInformation> getPriceInformationForProducts(List<ProductModel> models);

}
