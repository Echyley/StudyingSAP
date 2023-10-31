/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services;

import java.util.List;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException;
/**
 * Get the price information for the  product
 *
 */
public interface SapS4OMPricingService {
	
	/**
	 * Get the Price information for the product
	 *
	 * @param product the ProductModel
	 * l
	 * @return list of PriceInformation for the corresponding product 
	 * @throws de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException 
	 */
	List<PriceInformation> getPriceForProduct(ProductModel product) throws OutboundServiceException;
}
