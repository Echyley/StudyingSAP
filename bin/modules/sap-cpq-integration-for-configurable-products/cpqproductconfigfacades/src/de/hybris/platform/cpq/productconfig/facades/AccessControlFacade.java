/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades;

import de.hybris.platform.cpq.productconfig.facades.data.AccessControlData;


/**
 * Facade for client authentication and authorization
 */
public interface AccessControlFacade
{
	/**
	 * Authenticates and authorizes a client to CPQ
	 *
	 * @return authorization data
	 */
	AccessControlData performAccessControlForClient();
}
