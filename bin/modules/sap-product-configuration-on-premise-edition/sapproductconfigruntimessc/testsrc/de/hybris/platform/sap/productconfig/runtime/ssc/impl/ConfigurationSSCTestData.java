/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticValueData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticValueData;


/**
 * Helper class for tests
 *
 */
public class ConfigurationSSCTestData
{

	public static ICsticValueData createCsticValueData()
	{
		final CsticValueData data = new CsticValueData();
		return data;
	}

}
