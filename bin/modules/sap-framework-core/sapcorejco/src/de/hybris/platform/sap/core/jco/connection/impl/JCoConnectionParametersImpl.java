/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.connection.impl;

import de.hybris.platform.sap.core.jco.connection.JCoConnectionParameter;
import de.hybris.platform.sap.core.jco.connection.JCoConnectionParameters;
import de.hybris.platform.sap.core.jco.constants.SapcorejcoConstants;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * JCo Connection parameter container.
 */
public class JCoConnectionParametersImpl implements JCoConnectionParameters
{

	/**
	 * Logger.
	 */
	static final Logger log = Logger.getLogger(JCoConnectionParametersImpl.class.getName());

	private final Map<String, JCoConnectionParameter> connectionParameters = new HashMap<String, JCoConnectionParameter>();

	@Override
	public void addConnectionParameter(final JCoConnectionParameter connectionParameter)
	{
		final String functionModule = connectionParameter.getFunctionModule();
		if (connectionParameters.containsKey(functionModule))
		{
			final String msg = MessageFormat.format("JCo Connection Parameter for function module {0} will be overwritten!",
					new Object[]
					{ functionModule });
			log.log(Level.ERROR, msg);
		}
		connectionParameters.put(functionModule, connectionParameter);
	}

	@Override
	public JCoConnectionParameter getConnectionParameter(final String functionModule)
	{
		return connectionParameters.get(functionModule);
	}

	@Override
	public Map<String, JCoConnectionParameter> getConnectionParameterMap()
	{
		return Collections.unmodifiableMap(connectionParameters);
	}

	@Override
	public String toString()
	{
		return super.toString() + SapcorejcoConstants.CRLF + "Connection Parameters: " + SapcorejcoConstants.CRLF
				+ getConnectionParameterMap();
	}

	@Override
	public boolean isFunctionModuleConfigured(final String functionModule)
	{
		return connectionParameters.containsKey(functionModule);
	}


}
