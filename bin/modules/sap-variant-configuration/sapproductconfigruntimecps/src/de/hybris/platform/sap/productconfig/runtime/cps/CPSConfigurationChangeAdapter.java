/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps;

import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;


/**
 * Prepares input for characteristic value change(s)
 */
public interface CPSConfigurationChangeAdapter
{

	/**
	 * Prepare changed configuration as input for service from config model
	 *
	 * @param model
	 *           config model
	 * @return changes to be sent to service
	 */
	CPSConfiguration prepareChangedConfiguration(ConfigModel model);

}
