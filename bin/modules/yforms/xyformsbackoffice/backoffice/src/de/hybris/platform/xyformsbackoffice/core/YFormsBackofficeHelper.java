/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.xyformsbackoffice.core;

import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;


public final class YFormsBackofficeHelper
{
	private static final Logger LOG = Logger.getLogger(YFormsBackofficeHelper.class);

	private YFormsBackofficeHelper() {

	}

	public static boolean isUserInNotAllowedRoles(final Collection<String> rolesNotAllowed, final PrincipalModel principalModel)
	{
		final Set<PrincipalGroupModel> groups = principalModel.getGroups();

		for (final PrincipalGroupModel g : groups)
		{
			final String name = g.getUid();
			if (rolesNotAllowed.contains(name))
			{
				LOG.debug(name + " is not allowed to see system records");
				return true;
			}
		}

		return false;
	}

}
