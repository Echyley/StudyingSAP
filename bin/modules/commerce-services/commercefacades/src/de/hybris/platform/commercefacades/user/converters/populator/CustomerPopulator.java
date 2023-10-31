/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.user.converters.populator;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.SitePreferenceData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.SitePreferenceModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Converter implementation for {@link de.hybris.platform.core.model.user.UserModel} as source and
 * {@link de.hybris.platform.commercefacades.user.data.CustomerData} as target type.
 */
public class CustomerPopulator implements Populator<CustomerModel, CustomerData>
{
	private Converter<CurrencyModel, CurrencyData> currencyConverter;
	private Converter<LanguageModel, LanguageData> languageConverter;
	private CustomerNameStrategy customerNameStrategy;
	private BaseSiteService baseSiteService;
	private Converter<SitePreferenceModel, SitePreferenceData> sitePreferenceConverter;

	protected Converter<CurrencyModel, CurrencyData> getCurrencyConverter()
	{
		return currencyConverter;
	}

	@Required
	public void setCurrencyConverter(final Converter<CurrencyModel, CurrencyData> currencyConverter)
	{
		this.currencyConverter = currencyConverter;
	}

	protected Converter<LanguageModel, LanguageData> getLanguageConverter()
	{
		return languageConverter;
	}

	@Required
	public void setLanguageConverter(final Converter<LanguageModel, LanguageData> languageConverter)
	{
		this.languageConverter = languageConverter;
	}

	protected BaseSiteService getBaseSiteService()
	{
		if (this.baseSiteService == null)
		{
			this.baseSiteService = Registry.getApplicationContext().getBean("baseSiteService", BaseSiteService.class);
		}
		return this.baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected Converter<SitePreferenceModel, SitePreferenceData> getSitePreferenceConverter()
	{
		if (this.sitePreferenceConverter == null)
		{
			this.sitePreferenceConverter = Registry.getApplicationContext().getBean("sitePreferenceConverter", Converter.class);
		}
		return this.sitePreferenceConverter;
	}

	@Required
	public void setSitePreferenceConverter(final Converter<SitePreferenceModel, SitePreferenceData> sitePreferenceConverter)
	{
		this.sitePreferenceConverter = sitePreferenceConverter;
	}

	protected CustomerNameStrategy getCustomerNameStrategy()
	{
		return customerNameStrategy;
	}

	@Required
	public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy)
	{
		this.customerNameStrategy = customerNameStrategy;
	}

	@Override
	public void populate(final CustomerModel source, final CustomerData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		if (source.getSessionCurrency() != null)
		{
			target.setCurrency(getCurrencyConverter().convert(source.getSessionCurrency()));
		}
		if (source.getSessionLanguage() != null)
		{
			target.setLanguage(getLanguageConverter().convert(source.getSessionLanguage()));
		}

		final String[] names = getCustomerNameStrategy().splitName(source.getName());
		if (names != null)
		{
			target.setFirstName(names[0]);
			target.setLastName(names[1]);
		}

		final TitleModel title = source.getTitle();
		if (title != null)
		{
			target.setTitleCode(title.getCode());
		}

		target.setName(source.getName());
		setUid(source, target);
		target.setCustomerId(source.getCustomerID());
		target.setDeactivationDate(source.getDeactivationDate());
		setSitePreference(source, target);
	}

	private void setSitePreference(final CustomerModel source, final CustomerData target)
	{
		// To prevent NPE when mapping from DTO we need to instantiate preferences.
		SitePreferenceData sitePreferenceData = new SitePreferenceData();
		if (Objects.nonNull(source.getSitePreferences()))
		{
			BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
			for (final SitePreferenceModel sitePreference : source.getSitePreferences())
			{
				if (sitePreference.getSite().equals(currentBaseSite))
				{
					sitePreferenceConverter.convert(sitePreference, sitePreferenceData);
					break;
				}
			}
		}
		target.setSitePreference(sitePreferenceData);
	}

	protected void setUid(final UserModel source, final CustomerData target)
	{
		target.setUid(source.getUid());
		if (source instanceof CustomerModel)
		{
			final CustomerModel customer = (CustomerModel) source;
			if (isOriginalUidAvailable(customer))
			{
				target.setDisplayUid(customer.getOriginalUid());
			}
		}
	}

	protected boolean isOriginalUidAvailable(final CustomerModel source)
	{
		return source.getOriginalUid() != null;
	}
}
