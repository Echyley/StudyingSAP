/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.job;

import de.hybris.platform.integrationservices.model.TypeDescriptor;


/**
 * The InfoExpressionGenerator generates an infoExpression that represents integrationKey parts for the provided item type.
 */
public interface InfoExpressionGenerator
{
	/**
	 * Generates an infoExpression String from the provided {@link TypeDescriptor} and item type.
	 *
	 * @param itemType - item type, for which the info expression should be generated. If {@code null}, then default info
	 *                 expression for the implementation should be returned.
	 * @return a String representing the generated infoExpression
	 */
	String generateInfoExpression(TypeDescriptor itemType);
}
