/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;

import java.io.IOException;
import java.io.Serializable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * Container for commerce session attributes used in with SSC
 */
public class SSCProductConfigSessionAttributeContainer implements Serializable
{
	private static final long serialVersionUID = 1L;

	@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
	private transient ConfigurationProvider configurationProvider;


	/**
	 * @return the configurationProvider
	 */
	public ConfigurationProvider getConfigurationProvider()
	{
		return configurationProvider;
	}

	/**
	 * @param configurationProvider
	 *           the configurationProvider to set
	 */
	public void setConfigurationProvider(final ConfigurationProvider configurationProvider)
	{
		this.configurationProvider = configurationProvider;
	}

	private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
	}

}
