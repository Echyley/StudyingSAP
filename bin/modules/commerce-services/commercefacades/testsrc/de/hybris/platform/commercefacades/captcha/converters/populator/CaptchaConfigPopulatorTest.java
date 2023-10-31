/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.captcha.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.captcha.data.CaptchaConfigData;
import de.hybris.platform.commerceservices.config.SiteConfigService;
import de.hybris.platform.store.BaseStoreModel;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CaptchaConfigPopulatorTest
{
	private final static String PUBLIC_KEY = "123";
	private static final String SITE_UID = "electronics";

	@InjectMocks
	private CaptchaConfigPopulator captchaConfigPopulator;

	@Mock
	private SiteConfigService siteConfigService;

	@Test
	public void testPopulate()
	{
		final CaptchaConfigData captchaConfigData = new CaptchaConfigData();
		final BaseStoreModel baseStoreModel = new BaseStoreModel();
		baseStoreModel.setCaptchaCheckEnabled(true);

		final BaseSiteModel baseSiteModel = new BaseSiteModel();
		baseSiteModel.setUid(SITE_UID);
		baseSiteModel.setStores(List.of(baseStoreModel));

		when(this.siteConfigService.getProperty(SITE_UID, CaptchaConfigPopulator.RECAPTCHA_SITE_KEY_PROPERTY)).thenReturn(
				PUBLIC_KEY);

		this.captchaConfigPopulator.populate(baseSiteModel, captchaConfigData);

		assertThat(captchaConfigData.isEnabled()).isTrue();
		assertThat(captchaConfigData.getPublicKey()).isEqualTo(PUBLIC_KEY);
	}
}
