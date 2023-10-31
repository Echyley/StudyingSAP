/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services;

import java.util.List;
import java.util.Map;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException;
import de.hybris.platform.store.BaseStoreModel;

/**
 * 
 * Service to fetch the price, stock and also to verify the credit limit
 *
 */
public interface SapS4SalesOrderSimulationService {
	
	/**
	 * Get all price information for given product.
	 *
	 * @param productModel the product model
	 * @return map containing stock information as well as price information.
	 * @throws de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException 
	 */
	public List<PriceInformation> getPriceDetailsForProduct(ProductModel productModel) throws OutboundServiceException;
	
	/**
	 * Get the stock level information  for the product.
	 *
	 * @param productModel the ProductModel
	 * @param baseStore   BaseStoreModel
	 * @return containing SapS4OMProductAvailability information for the corresponding plants.
	 * @throws de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException  
	 */
	public SapS4OMProductAvailability getStockAvailability(ProductModel productModel,BaseStoreModel baseStore) throws OutboundServiceException;
	

	/**
	 * Get the price level information for products.
	 *
	 * @param productModels list of products
	 * @return List containing priceInformation .
	 * @throws de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException  
	 */
	public Map<String, List<PriceInformation>> getPriceDetailsForProducts(List<ProductModel> productModels) throws OutboundServiceException;

	/**
	 * Gets the credit limit status for the Cart for the user.
	 *
	 * @param cartModel The cart Model
	 * @param user The User Model
	 * @return boolean value for credit limit
	 * @throws de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException  
	 */
	public Boolean checkCreditLimitExceeded(ItemModel cartModel, UserModel user) throws OutboundServiceException;
	
	/**
	 * Sets the live stock, price, discounts, delivery & total in cartModel and CartData.
	 *
	 * @param cartModel The cart Model
	 * @param CartData  The Cart Data
	 * @return
	 * @throws de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException  
	 */
	public void setCartDetails(AbstractOrderModel cartModel) throws OutboundServiceException;

}
