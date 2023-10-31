/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.checkout.pci.impl;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.payment.constants.PaymentConstants;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class ConfiguredCheckoutPciStrategy extends AbstractCheckoutPciStrategy
{
	private static final String DEFAULT_PCI_STRATEGY = "SOP";

	private SiteConfigService siteConfigService;


	protected SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	@Required
	public void setSiteConfigService(final SiteConfigService siteConfigService)
	{
		this.siteConfigService = siteConfigService;
	}

	@Override
	protected boolean canSupport()
	{
		return true;
	}

	@Override
	public CheckoutPciOptionEnum getSubscriptionPciOption()
	{
		final CheckoutPciOptionEnum checkoutPciOption = getSiteConfiguredSubscriptionPciOption();
		if (checkoutPciOption != null)
		{
			return checkoutPciOption;
		}
		return getDefaultCheckoutPciStrategy().getSubscriptionPciOption();
	}

	protected CheckoutPciOptionEnum getSiteConfiguredSubscriptionPciOption()
	{
		//Check if there is any site specific and ui experience specific PCI configuration
		final String pciOption = getSiteConfigService().getProperty(PaymentConstants.PaymentProperties.SITE_PCI_STRATEGY);
		if(StringUtils.isNotBlank(pciOption))
		{
			return CheckoutPciOptionEnum.valueOf(pciOption);
		}
		// Check if there is any HOP configuration
		return CheckoutPciOptionEnum.valueOf(getSiteConfigService().getString(PaymentConstants.PaymentProperties.SITE_PCI_STRATEGY,
				DEFAULT_PCI_STRATEGY));
	}
}
