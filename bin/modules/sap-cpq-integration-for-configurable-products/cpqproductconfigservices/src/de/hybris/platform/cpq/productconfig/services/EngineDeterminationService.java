/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services;

/**
 * Determines the configuration engine we use
 */
public interface EngineDeterminationService
{

	/**
	 * @return Are we using the mock configuration engine?
	 */
	boolean isMockEngineActive();

}
