/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.backoffice.editors.actionseditor;

import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;

import java.util.List;


/**
 * Provides a list of product codes used in "Currently configuring product" conditions in a product configuration rule
 */
public interface ProductconfigProductCodeExtractor
{

	/**
	 * Retrieves a list of product codes used in "Currently configuring product" conditions in a product configuration
	 * rule
	 *
	 * @param ruleModelRef
	 *           Productconfig source rule model
	 * @return List of product codes
	 */
	List<String> retrieveProductCodeList(ProductConfigSourceRuleModel ruleModelRef);

}
