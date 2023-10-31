/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.basesites.converters.populator;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.basesite.data.BaseSiteData;
import de.hybris.platform.commercefacades.basestore.data.BaseStoreData;
import de.hybris.platform.commercefacades.captcha.data.CaptchaConfigData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;


/**
 * Populates {@link BaseSiteData} from {@link BaseSiteModel}
 */
public class BaseSitePopulator implements Populator<BaseSiteModel, BaseSiteData>
{
	private Converter<LanguageModel, LanguageData> languageConverter;
	private Converter<BaseStoreModel, BaseStoreData> baseStoreConverter;
	private Converter<BaseSiteModel, CaptchaConfigData> captchaConfigConverter;

	@Override
	public void populate(final BaseSiteModel source, final BaseSiteData target) throws ConversionException
	{
		target.setUid(source.getUid());
		target.setName(source.getName());
		target.setStores(getBaseStoreConverter().convertAll(source.getStores()));
		target.setLocale(source.getLocale());
		target.setRequiresAuthentication(source.isRequiresAuthentication());
		target.setDataIsolationEnabled(source.getDataIsolationEnabled());

		if (source.getChannel() != null)
		{
			target.setChannel(source.getChannel().getCode());
		}
		if (source.getTheme() != null)
		{
			target.setTheme(source.getTheme().getCode());
		}
		if (source.getDefaultLanguage() != null)
		{
			target.setDefaultLanguage(getLanguageConverter().convert(source.getDefaultLanguage()));
		}

		target.setCaptchaConfig(this.getCaptchaConfigConverter().convert(source));
	}

	protected Converter<LanguageModel, LanguageData> getLanguageConverter()
	{
		return languageConverter;
	}

	public void setLanguageConverter(final Converter<LanguageModel, LanguageData> languageConverter)
	{
		this.languageConverter = languageConverter;
	}

	protected Converter<BaseStoreModel, BaseStoreData> getBaseStoreConverter()
	{
		return baseStoreConverter;
	}

	public void setBaseStoreConverter(final Converter<BaseStoreModel, BaseStoreData> baseStoreConverter)
	{
		this.baseStoreConverter = baseStoreConverter;
	}

	protected Converter<BaseSiteModel, CaptchaConfigData> getCaptchaConfigConverter()
	{
		if (this.captchaConfigConverter == null)
		{
			this.captchaConfigConverter = Registry.getApplicationContext().getBean("captchaConfigConverter", Converter.class);
		}
		return this.captchaConfigConverter;
	}

	public void setCaptchaConfigConverter(final Converter<BaseSiteModel, CaptchaConfigData> captchaConfigConverter)
	{
		this.captchaConfigConverter = captchaConfigConverter;
	}

}
