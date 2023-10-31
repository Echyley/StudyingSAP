/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductconfigsomservices.cart;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtservices.cart.CartService;


/**
 * Service for updating cart data like Adding/updating cart entries for configurable product.
 */
public interface CPQCartService extends CartService
{

	/**
	 * Adds a new configuration to the cart. A new item will be created, based on the root instance of the config model
	 *
	 * @param configModel
	 *           Configutation model, which will be added to the cart
	 * @return Key of new item
	 */
	String addConfigurationToCart(ConfigModel configModel);

	/**
	 * Updates the configuration attached to an item
	 *
	 * @param key
	 *           Key of existing item
	 * @param configModel
	 *           Configuration
	 * @return Key of updated item
	 */
	String updateConfigurationInCart(String key, ConfigModel configModel);

	/**
	 * Does an item exist with a given key?
	 *
	 * @param itemKey
	 *           key for the item.
	 * @return Does item exist for the item key?
	 */
	Item getItemByKey(String itemKey);


}
