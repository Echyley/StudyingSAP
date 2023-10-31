/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.subscriptionservices.search.solrfacetsearch.provider.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.impl.AbstractPropertyFieldValueProvider;
import de.hybris.platform.subscriptionservices.subscription.SubscriptionProductService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;



/**
 * Extension of {@link AbstractPropertyFieldValueProvider} with {@link SubscriptionProductService}.
 */
public abstract class SubscriptionAwareFieldValueProvider extends AbstractPropertyFieldValueProvider
{
	private SubscriptionProductService subscriptionProductService;
	private CommonI18NService commonI18NService;
	private SessionService sessionService;
	private FieldNameProvider fieldNameProvider;
	/**
	 * @return subscription product service
	 */
	protected SubscriptionProductService getSubscriptionProductService()
	{
		return subscriptionProductService;
	}

	public void setSubscriptionProductService(final SubscriptionProductService subscriptionProductService)
	{
		this.subscriptionProductService = subscriptionProductService;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected FieldNameProvider getFieldNameProvider()
	{
		return fieldNameProvider;
	}

	public void setFieldNameProvider(final FieldNameProvider fieldNameProvider)
	{
		this.fieldNameProvider = fieldNameProvider;
	}

	protected Collection<FieldValue> getSubscriptionFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model)
	{
		validateParameterNotNullStandardMessage("model", model);
		validateParameterNotNullStandardMessage("indexedProperty", indexedProperty);

		final List<FieldValue> fieldValues = new ArrayList<>();

		if (indexedProperty.isLocalized())
		{
			final Collection<LanguageModel> languages = indexConfig.getLanguages();
			for (final LanguageModel language : languages)
			{
				final Object value = getSessionService().executeInLocalView(new SessionExecutionBody()
				{
					@Override
					public Object execute()
					{
						getCommonI18NService().setCurrentLanguage(language);
						return getPropertyValue(model);
					}
				});

				if (value != null)
				{
					final Collection<String> fieldNames = getFieldNameProvider().getFieldNames(indexedProperty, language.getIsocode());
					fieldValues.addAll(fieldNames.stream().map(fieldName -> new FieldValue(fieldName, value)).collect(Collectors.toList()));
				}
			}
		}

		return fieldValues;
	}

	protected Object getPropertyValue(final Object model) {
		return null;
	}
}
