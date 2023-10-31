/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductconfigsomservices.bolfacade;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolCartFacade;


/**
 * Facade to the BOL layer. Allows to work with the BOL cart and configurable product.
 */
public interface CPQBolCartFacade extends BolCartFacade
{
	/**
	 * Adds a configuration to the cart, adding a new item with the config model attached.
	 *
	 * @param configModel
	 *           configuration model
	 * @return Key of new item
	 */
	String addConfigurationToCart(ConfigModel configModel);

	/**
	 * Updates the configuration attached to an item
	 *
	 * @param key
	 *           Item key
	 * @param configModel
	 *           Configuration
	 * @return Key of updated item
	 */
	String updateConfigurationInCart(String key, ConfigModel configModel);

}
