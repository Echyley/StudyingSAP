/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.jalo;

import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.util.localization.Localization;


/**
 * CMS Restruction for configurable products within cart
 */
public class CMSCartConfigurationRestriction extends GeneratedCMSCartConfigurationRestriction
{

	@Override
	public String getDescription(final SessionContext ctx)
	{
		return Localization.getLocalizedString("type.CMSCartConfigurationRestriction.description.text");
	}

}
