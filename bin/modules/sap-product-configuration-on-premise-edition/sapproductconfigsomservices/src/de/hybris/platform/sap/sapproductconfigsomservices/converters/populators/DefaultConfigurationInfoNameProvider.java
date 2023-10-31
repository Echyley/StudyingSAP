/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductconfigsomservices.converters.populators;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;



public class DefaultConfigurationInfoNameProvider implements ConfigurationInfoNameProvider
{


	@Override
	public String getCharacteristicDisplayName(final CsticModel cstic)
	{
		return cstic.getLanguageDependentName();
	}


	@Override
	public String getValueDisplayName(final CsticModel cstic, final CsticValueModel value)
	{
		return value.getLanguageDependentName();
	}

}
