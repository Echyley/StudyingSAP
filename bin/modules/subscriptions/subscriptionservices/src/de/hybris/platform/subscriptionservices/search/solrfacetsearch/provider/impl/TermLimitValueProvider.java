/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.subscriptionservices.search.solrfacetsearch.provider.impl;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;
import de.hybris.platform.subscriptionservices.enums.TermOfServiceFrequency;
import de.hybris.platform.subscriptionservices.model.SubscriptionTermModel;

import java.io.Serializable;
import java.util.Collection;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.annotation.Required;


/**
 * This ValueProvider will provide the value for a subscription {@link ProductModel}'s service term limit which is the
 * combination of its TermOfServiceNumber and TermOfServiceFrequency, e.g. "12 Month(s)"
 */
public class TermLimitValueProvider extends SubscriptionAwareFieldValueProvider implements FieldValueProvider, Serializable
{
	private transient TypeService typeService;

	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig,@Nonnull final IndexedProperty indexedProperty,
			@Nonnull final Object model) throws FieldValueProviderException
	{
		return getSubscriptionFieldValues(indexConfig, indexedProperty, model);
	}

	@Override
	protected Object getPropertyValue(final Object model)
	{
		if (!(model instanceof ProductModel))
		{
			return null;
		}
			
		final SubscriptionTermModel subscriptionTerm = ((ProductModel) model).getSubscriptionTerm();
		if (subscriptionTerm == null)
		{
			return null;
		}
				
		final TermOfServiceFrequency termOfServiceFrequency = subscriptionTerm.getTermOfServiceFrequency();
		final Integer termOfServiceNumber = subscriptionTerm.getTermOfServiceNumber();
		if (termOfServiceFrequency == null)
		{
				return null;
		}
				
		String locName = getTypeService().getEnumerationValue(TermOfServiceFrequency._TYPECODE,
					termOfServiceFrequency.getCode()).getName();
		if (locName == null)
		{
			// no localization for current language -> take code
			locName = termOfServiceFrequency.getCode();
		}
		return (termOfServiceNumber == null ? "" : termOfServiceNumber.toString() + " ") + locName;

	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

}
