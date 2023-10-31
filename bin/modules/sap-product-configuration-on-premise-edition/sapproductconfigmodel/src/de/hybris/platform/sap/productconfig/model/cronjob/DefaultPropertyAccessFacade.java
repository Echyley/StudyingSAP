/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.model.cronjob;

import de.hybris.platform.sap.productconfig.model.constants.SapproductconfigmodelConstants;
import de.hybris.platform.util.Config;


/**
 * Accessing properties for SSC DB attributes
 */
public class DefaultPropertyAccessFacade implements PropertyAccessFacade
{

	@Override
	public boolean getStartDeltaloadAfterInitial()
	{
		return Config.getBoolean(SapproductconfigmodelConstants.START_DELTA_LOAD_AFTER_INITIAL, true);
	}

}
