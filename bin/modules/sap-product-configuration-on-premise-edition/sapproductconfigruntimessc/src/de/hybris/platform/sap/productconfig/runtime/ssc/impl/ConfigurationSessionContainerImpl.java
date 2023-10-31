/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.sap.productconfig.runtime.ssc.ConfigurationSessionContainer;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;


/**
 * Default implementation of {@link ConfigurationSessionContainer}
 */
public class ConfigurationSessionContainerImpl implements ConfigurationSessionContainer
{
	public static final int DEFAULT_NUMBER_OF_LINES_FOR_LOG_OUTPUT = 10;
	public static final int NUMBER_OF_LINES_TO_SKIP = 3;
	private static final Logger LOG = Logger.getLogger(ConfigurationSessionContainerImpl.class);
	private int maxNumberOfConfigurations = 0;
	private final Map<String, IConfigSession> sessionMap = Collections.synchronizedMap(new LinkedHashMap<String, IConfigSession>() {
		@Override
		protected boolean removeEldestEntry(final Map.Entry<String, IConfigSession> eldest)  {
			if(maxNumberOfConfigurations > 0 && size() > maxNumberOfConfigurations) {
				if(eldest.getValue() != null) {
					eldest.getValue().closeSession();
				}
				return true;
			}
			return false;
		}
	});

	@Override
	public Map<String, IConfigSession> getSessionMap()
	{
		return sessionMap;
	}



	@Override
	public void storeConfiguration(final String qualifiedId, final IConfigSession configSession)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Store configuration for: " + qualifiedId);
		}
		sessionMap.put(qualifiedId, configSession);
	}

	@Override
	public IConfigSession retrieveConfigSession(final String qualifiedId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Retrieve configuration for: " + qualifiedId);
		}
		final IConfigSession configSession = sessionMap.get(qualifiedId);

		if (configSession == null)
		{
			throw new IllegalStateException(new StringBuilder().append("Session for id ").append(qualifiedId)
					.append(" does not exist and could not be retrieved").toString());
		}
		return configSession;
	}

	@Override
	public void releaseSession(final String qualifiedId)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Release configuration for: " + qualifiedId);
			LOG.debug("Release called from: " + getTopLinesOfStacktrace(DEFAULT_NUMBER_OF_LINES_FOR_LOG_OUTPUT).toString());
		}
		final IConfigSession removedSession = sessionMap.remove(qualifiedId);
		if (null != removedSession)
		{
			removedSession.closeSession();
		}
	}


	protected StringBuilder getTopLinesOfStacktrace(final int numberOfLines)
	{

		final List<StackTraceElement> stackTrace = Arrays.asList(Thread.currentThread().getStackTrace());
		final StringBuilder result = new StringBuilder("\n");
		int counter = 0;
		for (final StackTraceElement line : stackTrace)
		{
			if (++counter > NUMBER_OF_LINES_TO_SKIP)
			{
				result.append(line).append("\n");
			}
			if (counter > numberOfLines + NUMBER_OF_LINES_TO_SKIP)
			{
				break;
			}
		}
		return result;
	}

	/**
	 * Getter for maxNumberOfConfigurations
	 * 
	 * @return maxNumberOfConfigurations
	 */
	public int getMaxNumberOfConfigurations()
	{
		return maxNumberOfConfigurations;
	}

	/**
	 * Setter for maxNumberOfConfigurations. This property specifies the maximum number of configurations which will be kept
	 * per user session. A negative number means infinite.
	 * 
	 * @param maxNumberOfConfigurations
	 */
	public void setMaxNumberOfConfigurations(final int maxNumberOfConfigurations)
	{
		this.maxNumberOfConfigurations = maxNumberOfConfigurations;
	}
}
