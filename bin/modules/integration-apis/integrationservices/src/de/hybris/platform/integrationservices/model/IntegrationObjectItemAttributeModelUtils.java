/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.model;

/**
 * Utilities used in integration object models.
 */
public final class IntegrationObjectItemAttributeModelUtils
{
	private IntegrationObjectItemAttributeModelUtils()
	{}

	/**
	 * Indicates if an attribute is unique. If either the IntegrationObjectionAttributeDefinitionModel OR
	 * the model's attribute descriptor is unique, the attribute is unique
	 *
	 * @param attributeDefinitionModel Model to test for uniqueness
	 * @return true if unique, else false
	 */
	public static boolean isUnique(final IntegrationObjectItemAttributeModel attributeDefinitionModel)
	{
		final Boolean modelUnique = attributeDefinitionModel.getUnique();
		final boolean descriptorUnique = attributeDefinitionModel.getAttributeDescriptor() != null &&
				falseIfNull(attributeDefinitionModel.getAttributeDescriptor().getUnique());

		return falseIfNull(modelUnique) || descriptorUnique;
	}

	/**
	 * Returns false if the value is null, otherwise the value is returned
	 * @param value boolean value for the attribute's property that is being evaluated
	 * @return value if non-null, otherwise false
	 */
	public static boolean falseIfNull(final Boolean value)
	{
		return value != null && value;
	}
}
