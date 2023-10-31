/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.kymaintegrationservices.event.impl;

import de.hybris.platform.apiregistryservices.dto.EventSourceData;
import de.hybris.platform.kymaintegrationservices.event.KymaEventFilterService;
import de.hybris.platform.scripting.engine.ScriptExecutable;
import de.hybris.platform.scripting.engine.ScriptingLanguagesService;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * Default implementation of the interface {@link KymaEventFilterService} to filter Kyma events
 * based on the result of execution a Groovy script attached to the event configuration.
 */
public class KymaEventFilterServiceImpl implements KymaEventFilterService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(KymaEventFilterServiceImpl.class);
	private static final String SCRIPT_ARGUMENT = "event";
	private final ScriptingLanguagesService scriptingLanguagesService;

	public KymaEventFilterServiceImpl(@NotNull final ScriptingLanguagesService scriptingLanguagesService)
	{
		Preconditions.checkArgument(scriptingLanguagesService != null, "Scripting language service cannot be null!");
		this.scriptingLanguagesService = scriptingLanguagesService;
	}

	/**
	 * The filtering logic is implemented in the attached script of the event configuration (e.g., model://kymaOrderFilter).
	 * The filter script receives the event through an input argument called <b>event</b> and should return true or false.
	 * If the filter script fails to execute successfully, an error message will be logged and returns false.
	 *
	 * @param eventSourceData Contains Kyma event and its configuration
	 * @return A boolean flag indicates if the event will be sent to Kyma or not.
	 * If the flag is true, the event will be sent; otherwise the event will not be sent.
	 */
	@Override
	public boolean filterKymaEvent(final EventSourceData eventSourceData)
	{
		final String scriptUri = eventSourceData.getEventConfig().getFilterLocation();
		final String eventClass = eventSourceData.getEvent().getClass().getName();
		try
		{
			if (StringUtils.isNotBlank(scriptUri))
			{
				final Map<String, Object> scriptArguments = new HashMap<>();
				scriptArguments.put(SCRIPT_ARGUMENT, eventSourceData.getEvent());

				final ScriptExecutable executable = scriptingLanguagesService.getExecutableByURI(scriptUri);
				final String executionResult = executable.execute(scriptArguments).getScriptResult().toString();

				LOGGER.debug("The Kyma event [{}] filter script [{}] has been executed successfully and returned [{}]!",
						eventClass, scriptUri, executionResult);

				return Boolean.parseBoolean(executionResult);
			}
			else
			{
				LOGGER.debug("The Kyma event [{}] is not configured to be filtered!", eventClass);
				return true;
			}
		}
		catch (final RuntimeException ex)
		{
			LOGGER.error(
					"Failed to execute the filtering script [{}] for the Kyma event [{}]; therefore, the event will not be sent to Kyma!",
					scriptUri, eventClass, ex);
			return false;
		}
	}

}
