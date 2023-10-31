/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;

import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigSessionClient;


/**
 * Responsible for updating a configuration to SSC
 */
public interface ConfigurationUpdateAdapter
{

	/**
	 * Updates a configuration in SSC
	 *
	 * @param configModel
	 *           config model
	 * @param plainId
	 * @param session
	 *           corresponding config session
	 * @return <code>true</code>, only if it was necessary to send an updare to the configuration egnine
	 */
	boolean updateConfiguration(ConfigModel configModel, String plainId, IConfigSessionClient session);

}
