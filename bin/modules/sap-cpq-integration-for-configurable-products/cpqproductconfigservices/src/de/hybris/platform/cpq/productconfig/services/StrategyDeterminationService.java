/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services;

/**
 * Simple service to select the proper strategy based on the current active engine.
 *
 * @param <T>
 *           Strategy interface
 */
public interface StrategyDeterminationService<T>
{

	/**
	 * @return strategy for the current active engine
	 */
	T get();

}
