/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl.info;

import de.hybris.platform.integrationservices.model.TypeDescriptor;

/**
 * Generates a SpEL expression to derive an item type.
 */
public class ItemTypeExpressionGenerator implements ExpressionGenerator
{
	/**
	 * {@inheritDoc}
	 *
	 * @return a SpEL expression to derive an {@code ItemModel} type code; or {@code null}, if the {@code itemType} is {@code null}.
	 */
	@Override
	public String generateExpression(final TypeDescriptor itemType)
	{
		return itemType != null	? "#{itemtype}" : null;
	}
}
