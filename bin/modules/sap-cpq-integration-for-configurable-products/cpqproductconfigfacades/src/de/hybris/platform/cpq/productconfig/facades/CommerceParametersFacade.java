/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades;

import de.hybris.platform.cpq.productconfig.facades.data.CommerceParameters;


/**
 * Commerce parameters for CPQ
 */
public interface CommerceParametersFacade
{
	/**
	 * @return commerce parameters
	 */
	CommerceParameters prepareCommerceParameters();
}
