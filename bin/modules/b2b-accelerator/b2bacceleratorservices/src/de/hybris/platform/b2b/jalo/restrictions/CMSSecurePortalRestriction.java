/*
 *  
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.jalo.restrictions;

import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.util.localization.Localization;

import org.apache.log4j.Logger;

public class CMSSecurePortalRestriction extends GeneratedCMSSecurePortalRestriction
{
	private static final Logger LOG = Logger.getLogger(CMSSecurePortalRestriction.class.getName());


	/**
	 * @deprecated Since 5.4. use
	 *             {@link de.hybris.platform.b2b.model.restrictions.CMSSecurePortalRestrictionModel#getDescription()}
	 *             instead.
	 */
	@Deprecated(since = "5.4")
	@Override
	public String getDescription(SessionContext sessionContext)
	{
		return Localization.getLocalizedString("type.CMSSecurePortalRestriction.description.text");
	}
}
