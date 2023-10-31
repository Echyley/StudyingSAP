/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.wrapper;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.imp.ConfigSessionImpl;

/**
 * Wraps the creation of an IConfigSession in order to decouple code
 */
public class SSCConfigSessionFactory
{
	public IConfigSession provideInstance()
	{
		return new ConfigSessionImpl();
	}
}
