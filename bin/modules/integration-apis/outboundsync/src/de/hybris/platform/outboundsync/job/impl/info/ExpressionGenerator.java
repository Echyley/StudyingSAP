/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl.info;

import de.hybris.platform.integrationservices.model.TypeDescriptor;

import javax.annotation.Nullable;

/**
 * A common interface for generating expressions based on the provided integration object item type.
 * Each implementation is responsible for generation of one kind of an expression.
 */
public interface ExpressionGenerator
{
	/**
	 * Generates an expression
	 *
	 * @param itemType an item type to generate an expression for.
	 * @return an expression created based on the provided {@code itemType}. An implementation may return {@code null} or
	 * empty/blank expression.
	 */
	@Nullable
	String generateExpression(TypeDescriptor itemType);
}
