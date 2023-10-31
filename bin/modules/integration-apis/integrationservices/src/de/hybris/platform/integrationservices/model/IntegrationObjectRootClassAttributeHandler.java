/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model;

import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;

/**
 * Provides calculation of the dynamic {@code rootClass} attribute on the {@code IntegrationObjectModel}
 */
public class IntegrationObjectRootClassAttributeHandler
		extends AbstractDynamicAttributeHandler<IntegrationObjectClassModel, IntegrationObjectModel>
{

	/**
	 * Identifies the root {@code IntegrationObjectClassModel} for the {@code IntegrationObjectModel}
	 *
	 * @param integrationObject a model object to get root class from.
	 * @return {@code IntegrationObjectClassModel}. This root class model contains root attribute that is true.
	 * If {@code integrationObjectModel} does not have root class, a null value is returned; if it
	 * has more than one root classes, an exception will be raised.
	 */
	@Override
	@Nullable
	public IntegrationObjectClassModel get(final IntegrationObjectModel integrationObject)
	{
		final List<IntegrationObjectClassModel> rootClasses = CollectionUtils
				.emptyIfNull(integrationObject.getClasses())
				.stream()
				.filter(IntegrationObjectClassModel::getRoot)
				.toList();

		return rootClasses.isEmpty() ? null : rootClasses.get(0);
	}
}
