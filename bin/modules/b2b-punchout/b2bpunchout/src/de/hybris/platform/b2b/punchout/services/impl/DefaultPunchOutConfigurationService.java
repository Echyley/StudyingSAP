/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.services.impl;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.b2b.punchout.services.PunchOutConfigurationService;
import de.hybris.platform.b2b.punchout.services.PunchOutSessionService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.site.BaseSiteService;

import org.apache.commons.lang.StringUtils;


/**
 * Default implementation of {@link PunchOutConfigurationService}.
 */
public class DefaultPunchOutConfigurationService implements PunchOutConfigurationService
{

	private BaseSiteService baseSiteService;
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
	private ConfigurationService configurationService;
	private PunchOutSessionService punchoutSessionService;
	private String punchOutSessionUrlPath;

	@Override
	public String getPunchOutLoginUrl()
	{
		final String sessionId = getPunchOutSessionService().getCurrentPunchOutSessionId();
		if (StringUtils.isNotBlank(sessionId))
		{
			return getSiteBaseUrlResolutionService().getWebsiteUrlForSite(getBaseSiteService().getCurrentBaseSite(), "", true,
					getPunchOutSessionUrlPath(), "sid=" + sessionId);
		}
		return null;
	}


	@Override
	public String getDefaultCostCenter()
	{
		return getConfigurationService().getConfiguration().getString("b2bpunchout.checkout.costcenter.default");
	}

	@Override
	public boolean isOrderVerificationEnabled()
	{
		return getConfigurationService().getConfiguration().getBoolean("b2bpunchout.order.verification.enabled");
	}


	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected SiteBaseUrlResolutionService getSiteBaseUrlResolutionService()
	{
		return siteBaseUrlResolutionService;
	}

	public void setSiteBaseUrlResolutionService(final SiteBaseUrlResolutionService siteBaseUrlResolutionService)
	{
		this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected PunchOutSessionService getPunchOutSessionService()
	{
		return punchoutSessionService;
	}

	public void setPunchOutSessionService(final PunchOutSessionService punchoutSessionService)
	{
		this.punchoutSessionService = punchoutSessionService;
	}

	protected String getPunchOutSessionUrlPath()
	{
		return punchOutSessionUrlPath;
	}

	public void setPunchOutSessionUrlPath(final String punchOutSessionUrlPath)
	{
		this.punchOutSessionUrlPath = punchOutSessionUrlPath;
	}

}
