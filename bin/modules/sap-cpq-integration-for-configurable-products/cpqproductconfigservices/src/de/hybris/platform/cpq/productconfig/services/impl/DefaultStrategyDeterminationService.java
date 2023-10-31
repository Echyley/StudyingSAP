/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import de.hybris.platform.cpq.productconfig.services.EngineDeterminationService;
import de.hybris.platform.cpq.productconfig.services.StrategyDeterminationService;


/**
 * Simple service to select the proper strategy based on the current active engine.
 *
 * @param <T>
 *           Strategy interface
 */
public class DefaultStrategyDeterminationService<T> implements StrategyDeterminationService<T>
{

	private final EngineDeterminationService engineDeterminationService;
	private final T mockStrategey;
	private final T defaultStrategy;

	/**
	 * Default Constructor for injecting dependencies.
	 *
	 * @param engineDeterminationService
	 *           service to check whether to delegate to mock or default implementation
	 * @param defaultStrategy
	 *           default strategy
	 * @param mockStrategey
	 *           mock strategy
	 */
	public DefaultStrategyDeterminationService(final EngineDeterminationService engineDeterminationService,
			final T defaultStrategy, final T mockStrategey)
	{
		super();
		this.engineDeterminationService = engineDeterminationService;
		this.defaultStrategy = defaultStrategy;
		this.mockStrategey = mockStrategey;
	}

	@Override
	public T get()
	{
		if (getEngineDeterminationService().isMockEngineActive())
		{
			return getMockStrategy();
		}
		else
		{
			return getDefaultStrategy();
		}
	}

	protected EngineDeterminationService getEngineDeterminationService()
	{
		return engineDeterminationService;
	}

	protected T getMockStrategy()
	{
		return mockStrategey;
	}

	protected T getDefaultStrategy()
	{
		return defaultStrategy;
	}

}
