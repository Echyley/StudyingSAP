/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.integrationkey;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

public interface IntegrationKeyMetadataGenerator
{
	/**
	 * Generate a representation of the unique attributes of an item from its {@link TypeDescriptor}. This representation
	 * is the metadata of how an integrationKey is generated for a given descriptor's {@link IntegrationObjectItemModel}.
	 *
	 * @param descriptor type descriptor for the item to generate key metadata for
	 * @return a String representing the integrationKey metadata according the strategy defined in the implementation.
	 * If not implemented, this method will return an empty String as default.
	 */
	default String generateKeyMetadata(final TypeDescriptor descriptor)
	{
		return "";
	}

	/**
	 * Generate a representation of the unique attributes of the given item. This representation is the metadata of how an integrationKey
	 * is generated for a given {@link IntegrationObjectItemModel}.
	 *
	 * @param item the item to generate key metadata for
	 * @return a String representing the integrationKey metadata according the strategy defined in the implementation
	 * @deprecated use {@link #generateKeyMetadata(TypeDescriptor)} instead.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	String generateKeyMetadata(IntegrationObjectItemModel item);
}
