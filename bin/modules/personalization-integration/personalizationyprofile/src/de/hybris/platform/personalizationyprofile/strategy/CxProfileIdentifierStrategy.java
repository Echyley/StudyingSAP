/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.personalizationyprofile.strategy;

import de.hybris.platform.core.model.user.UserModel;


public interface CxProfileIdentifierStrategy
{
	String getProfileIdentifier(UserModel user);
}
