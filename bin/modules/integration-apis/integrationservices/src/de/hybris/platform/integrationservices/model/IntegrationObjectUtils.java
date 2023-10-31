/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model;

import java.util.Objects;

/**
 * Utilities used for {@link IntegrationObjectModel} and related models
 */
public final class IntegrationObjectUtils
{
	private IntegrationObjectUtils()
	{
	}

	/**
	 * Determines if two IntegrationObjectClassModels are equal.
	 *
	 * @param class1 {@link IntegrationObjectClassModel} to compare with class2
	 * @param class2 {@link IntegrationObjectClassModel} to compare with class1
	 * @return {@code true} if class1 is equal to class2, {@code false} if not
	 */
	public static boolean isEqual(final IntegrationObjectClassModel class1, final IntegrationObjectClassModel class2)
	{
		if (class1 == null)
		{
			return class2 == null;
		}

		return class2 != null
				&& Objects.equals(class1.getIntegrationObject().getCode(), class2.getIntegrationObject().getCode())
				&& Objects.equals(class1.getCode(), class2.getCode());
	}
}
