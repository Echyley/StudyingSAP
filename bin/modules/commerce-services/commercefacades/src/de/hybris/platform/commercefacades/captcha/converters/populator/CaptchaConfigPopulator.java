/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.captcha.converters.populator;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.captcha.data.CaptchaConfigData;
import de.hybris.platform.commerceservices.config.SiteConfigService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.store.BaseStoreModel;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;


public class CaptchaConfigPopulator implements Populator<BaseSiteModel, CaptchaConfigData>
{
	protected static final String RECAPTCHA_SITE_KEY_PROPERTY = "recaptcha.publickey";

	private SiteConfigService siteConfigService;

	@Override
	public void populate(final BaseSiteModel baseSiteModel, final CaptchaConfigData captchaConfigData) throws ConversionException
	{
		final List<BaseStoreModel> baseStores = baseSiteModel.getStores();
		captchaConfigData.setEnabled(!CollectionUtils.isEmpty(baseStores) && baseStores.stream().anyMatch(BaseStoreModel::getCaptchaCheckEnabled));
		captchaConfigData.setPublicKey(getSiteConfigService().getProperty(baseSiteModel.getUid(), RECAPTCHA_SITE_KEY_PROPERTY));
	}

	protected SiteConfigService getSiteConfigService()
	{
		return siteConfigService;
	}

	public void setSiteConfigService(final SiteConfigService siteConfigService)
	{
		this.siteConfigService = siteConfigService;
	}
}
