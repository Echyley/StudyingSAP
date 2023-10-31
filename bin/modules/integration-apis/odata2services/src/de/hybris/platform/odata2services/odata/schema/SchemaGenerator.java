/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.schema;

import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.TypeDescriptor;

import java.util.Collection;

import org.apache.olingo.odata2.api.edm.provider.Schema;

public interface SchemaGenerator
{
	/**
	 * Generates a {@link Schema} for an {@link IntegrationObjectItemModel} and all of its dependencies.
	 *
	 * @param allModelsForType a model and its dependencies
	 * @return a schema representing those types
	 * @deprecated This method can be used to generate schema for "traditional" integration object modeling type system items.
	 * However, we recommend to use the alternative {@code generate(Collection<TypeDescriptor>)} because it is
	 * universal and works for both: type system items and POJOs.
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	Schema generateSchema(final Collection<IntegrationObjectItemModel> allModelsForType);

	/**
	 * Generates a {@link Schema} for a given collection of {@link TypeDescriptor}s.
	 *
	 * @param descriptors a collection of type descriptors.
	 * @return a schema representing those types. If not implemented, this method will return an empty instance of {@link Schema}
	 * by default.
	 */
	default Schema generate(final Collection<TypeDescriptor> descriptors)
	{
		return new Schema();
	}
}
