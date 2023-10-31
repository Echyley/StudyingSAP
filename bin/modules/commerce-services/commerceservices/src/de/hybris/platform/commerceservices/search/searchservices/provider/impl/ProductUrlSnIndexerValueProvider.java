/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.search.searchservices.provider.impl;

import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.searchservices.core.service.SnSessionService;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;
import de.hybris.platform.searchservices.indexer.service.SnIndexerValueProvider;
import de.hybris.platform.searchservices.indexer.service.impl.AbstractSnIndexerValueProvider;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * Implementation of {@link SnIndexerValueProvider} for the product url.
 */
public class ProductUrlSnIndexerValueProvider extends AbstractSnIndexerValueProvider<ProductModel, String>
{
	public static final String ID = "productUrlSnIndexerValueProvider";

	protected static final Set<Class<?>> SUPPORTED_QUALIFIER_CLASSES = Set.of(LanguageModel.class);

	private UrlResolver<ProductModel> urlResolver;
	private I18NService i18nService;
	private SnSessionService snSessionService;

	@Override
	public Set<Class<?>> getSupportedQualifierClasses() throws SnIndexerException
	{
		return SUPPORTED_QUALIFIER_CLASSES;
	}

	@Override
	protected Object getFieldValue(final SnIndexerContext indexerContext, final SnIndexerFieldWrapper fieldWrapper,
			final ProductModel source, final String productUrl) throws SnIndexerException
	{
		if (fieldWrapper.isLocalized())
		{
			try
			{
				getSnSessionService().initializeSession();
				final Map<Locale, Object> value = new HashMap<>();
				// cannot use Collector as the productUrl can be null
				fieldWrapper.getQualifiers().stream().map(qualifier -> qualifier.getAs(Locale.class)).map(Locale.class::cast)
						.forEach(qualifier -> value.put(qualifier, getProductUrl(source, qualifier)));
				return value;
			}
			finally
			{
				getSnSessionService().destroySession();
			}
		}
		return getProductUrl(source);
	}

	protected String getProductUrl(final ProductModel product, final Locale locale)
	{
		getI18nService().setCurrentLocale(locale);
		return getProductUrl(product);
	}

	protected String getProductUrl(final ProductModel product)
	{
		return getUrlResolver().resolve(product);
	}

	protected UrlResolver<ProductModel> getUrlResolver()
	{
		return urlResolver;
	}

	@Required
	public void setUrlResolver(final UrlResolver<ProductModel> urlResolver)
	{
		this.urlResolver = urlResolver;
	}

	protected I18NService getI18nService()
	{
		return i18nService;
	}

	@Required
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
	}

	protected SnSessionService getSnSessionService()
	{
		return snSessionService;
	}

	@Required
	public void setSnSessionService(final SnSessionService snSessionService)
	{
		this.snSessionService = snSessionService;
	}
}
