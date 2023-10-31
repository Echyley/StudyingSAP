/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;


/**
 * Helper to determine the UI-type for a given Characteristic. It will determine how the characteristic is rendered on
 * the UI.
 */
public interface UiTypeFinder
{
	/**
	 * @param model
	 * @return UIType that decides how the characteristic is rendered on the UI
	 */
	UiType findUiTypeForCstic(CsticModel model);

	/**
	 * @param model
	 * @param data
	 * @return UIType that decides how the characteristic is rendered on the UI
	 */
	UiType findUiTypeForCstic(CsticModel model, CsticData data);


	/**
	 * @param csticModel
	 * @return UIValidatioType that decides how the user input for this characteristic is validated
	 */
	UiValidationType findUiValidationTypeForCstic(CsticModel csticModel);

}
