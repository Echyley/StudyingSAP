/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductconfigsomservices.converters.populators;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;


/**
 * Provides the language dependent names of characteristics and values.
 */
public interface ConfigurationInfoNameProvider
{
	/**
	 * @param cstic
	 *           Characteristic Model
	 * @return display name for the characteristic
	 */
	String getCharacteristicDisplayName(CsticModel cstic);

	/**
	 * @param cstic
	 *           Characteristic Model
	 * @param value
	 *           Value Model
	 * @return display name for the characteristic value
	 */
	String getValueDisplayName(CsticModel cstic, CsticValueModel value);
}
