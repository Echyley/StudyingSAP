/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.acceleratorservices.payment.cybersource.strategies.impl;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.payment.cybersource.constants.CyberSourceConstants;
import de.hybris.platform.acceleratorservices.payment.data.SubscriptionInfoData;
import de.hybris.platform.acceleratorservices.payment.strategies.SignatureValidationStrategy;
import de.hybris.platform.acceleratorservices.payment.utils.AcceleratorDigestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


public class DefaultSignatureValidationStrategy implements SignatureValidationStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSignatureValidationStrategy.class);
	private SiteConfigService siteConfigService;
	private AcceleratorDigestUtils digestUtils;

	@Override
	public boolean validateSignature(final SubscriptionInfoData subscriptionInfoData)
	{

		final String data = subscriptionInfoData.getSubscriptionSignedValue();
		final String signature = subscriptionInfoData.getSubscriptionIDPublicSignature();

		if (data == null || signature == null)
		{
			if (LOG.isWarnEnabled())
			{
				LOG.warn("Either Data or Signature is null in SubscriptionInfoData, returning false.");
			}
			return false;
		}

		try
		{
			final String computedSignature = getDigestUtils().getPublicDigest(data, getSharedSecret());
			return computedSignature.equals(signature);
		}
		catch (final Exception ex)
		{
			if (LOG.isInfoEnabled())
			{
				LOG.info("Failed to compute signature", ex);
			}
		}
		return false;

	}

	protected String getSiteConfigProperty(final String key)
	{
		return getSiteConfigService().getString(key, "");
	}

	protected SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	@Required
	public void setSiteConfigService(final SiteConfigService siteConfigService)
	{
		this.siteConfigService = siteConfigService;
	}

	/**
	 * Gets the CyberSource merchant's shared secret that is used to encrypt and validate connections.
	 * 
	 * @return the shared secret downloaded from the CyberSource Business Centre.
	 */
	protected String getSharedSecret()
	{
		return getSiteConfigProperty(CyberSourceConstants.HopProperties.SHARED_SECRET);
	}


	protected AcceleratorDigestUtils getDigestUtils()
	{
		return digestUtils;
	}

	@Required
	public void setDigestUtils(final AcceleratorDigestUtils digestUtils)
	{
		this.digestUtils = digestUtils;
	}

}
