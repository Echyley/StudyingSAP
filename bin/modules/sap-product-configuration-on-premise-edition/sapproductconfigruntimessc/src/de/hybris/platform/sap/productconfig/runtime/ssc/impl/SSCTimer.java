/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.impl.GenericTimer;

import org.apache.log4j.Logger;


/**
 * Helper class to enable to simple performance measurements of configuration engine calls. It will simple trace the
 * duration of every configuration engine call and log it with DEBUG severity.<br>
 * Set the LogLevel for this class to debug to enable the measurement.
 */
public class SSCTimer extends GenericTimer
{
	private static final String SSC = "SSC";
	private static final Logger LOG = Logger.getLogger(SSCTimer.class);

	@Override
	protected Logger getLogger()
	{
		return LOG;
	}

	@Override
	protected String getMeasuredDomainName()
	{
		return SSC;
	}
}
