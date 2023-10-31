/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.ssc;

/**
 * Initializes the ssc engine with connection properties to database
 */
public interface SSCEnginePopertiesInitializer
{
	/**
	 * Sets connection properties for ssc engine
	 */
	void initializeEngineProperties();
}
