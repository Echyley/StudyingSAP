/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderservices.dao;

import com.sap.sapcentralorderservices.model.SAPCentralOrderConfigurationModel;


/**
 * CentralOrderConfigurationDao
 */
public interface CentralOrderConfigurationDao
{

	/**
	 * Returns the configured SAPCentralOrderConfigurationModel
	 *
	 * @return {@link SAPCentralOrderConfigurationModel}
	 */
	SAPCentralOrderConfigurationModel findSapCentralOrderConfiguration();

}