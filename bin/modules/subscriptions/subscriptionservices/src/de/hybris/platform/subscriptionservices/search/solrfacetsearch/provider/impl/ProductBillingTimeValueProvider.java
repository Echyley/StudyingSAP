/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.subscriptionservices.search.solrfacetsearch.provider.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;
import de.hybris.platform.solrfacetsearch.provider.FieldValueProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * This ValueProvider will provide the value for a subscription {@link ProductModel}'s billing frequency attribute.
 */
public class ProductBillingTimeValueProvider extends SubscriptionAwareFieldValueProvider implements FieldValueProvider,
		Serializable
{
	@Override
	public Collection<FieldValue> getFieldValues(final IndexConfig indexConfig, final IndexedProperty indexedProperty,
			final Object model) throws FieldValueProviderException
	{
		return getSubscriptionFieldValues(indexConfig, indexedProperty, model);
	}

	@Override
	protected Object getPropertyValue(final Object model)
	{
		if (model instanceof ProductModel)
		{
			final ProductModel planProduct = (ProductModel) model;

			if (getSubscriptionProductService().isSubscription(planProduct)
				&& planProduct.getSubscriptionTerm() != null
				&& planProduct.getSubscriptionTerm().getBillingPlan() != null
				&& planProduct.getSubscriptionTerm().getBillingPlan().getBillingFrequency() != null)
			{
				return planProduct.getSubscriptionTerm().getBillingPlan().getBillingFrequency().getNameInCart();
			}
		}
		return null;
	}

}
