/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.evaluator.impl;

import de.hybris.platform.b2b.model.restrictions.CMSSecurePortalRestrictionModel;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.evaluator.CMSRestrictionEvaluator;
import de.hybris.platform.site.BaseSiteService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Evaluates a user restriction accordingly to context information.
 * <p/>
 */
public class CMSSecurePortalRestrictionEvaluator implements CMSRestrictionEvaluator<CMSSecurePortalRestrictionModel>
{
	private static final Logger LOG = Logger.getLogger(CMSSecurePortalRestrictionEvaluator.class);
	private BaseSiteService baseSiteService;

	@Override
	public boolean evaluate(final CMSSecurePortalRestrictionModel cmsUserRestriction, final RestrictionData context)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("isEnableRegistration: " + baseSiteService.getCurrentBaseSite().isEnableRegistration());
		}
		return baseSiteService.getCurrentBaseSite().isEnableRegistration();
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

}
